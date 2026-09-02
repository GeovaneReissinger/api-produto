package br.com.senai.produtosapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.senai.produtosapi.exception.ArmazenamentoException;
import br.com.senai.produtosapi.exception.ArquivoInvalidoException;
import br.com.senai.produtosapi.exception.ImagemNotFoundException;

/**
 * Responsável por gravar e ler os arquivos de imagem dos produtos na pasta local
 * "uploads": valida tipo/tamanho no upload, gera um nome único (UUID + nome original)
 * para evitar colisões e detecta o Content-Type na hora do download.
 */
@Service
public class FileStorageService {

    private static final Set<String> TIPOS_ACEITOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final Path pasta = Paths.get("uploads");

    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ArquivoInvalidoException("O arquivo de imagem não pode estar vazio.");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !TIPOS_ACEITOS.contains(contentType)) {
            throw new ArquivoInvalidoException(
                    "Tipo de arquivo inválido. Tipos aceitos: JPEG, PNG, GIF e WEBP.");
        }

        try {
            Files.createDirectories(pasta);

            String nomeOriginal = StringUtils.cleanPath(
                    StringUtils.hasText(arquivo.getOriginalFilename()) ? arquivo.getOriginalFilename() : "imagem");
            String nomeArquivo = UUID.randomUUID() + "_" + nomeOriginal;

            Path destino = pasta.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino);

            return nomeArquivo;
        } catch (IOException e) {
            throw new ArmazenamentoException("Erro ao armazenar o arquivo de imagem.", e);
        }
    }

    public Resource carregar(String nomeArquivo, Long produtoId) {
        try {
            Path caminho = pasta.resolve(nomeArquivo);
            Resource recurso = new UrlResource(caminho.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ImagemNotFoundException(produtoId);
            }
            return recurso;
        } catch (IOException e) {
            throw new ImagemNotFoundException(produtoId);
        }
    }

    // Content-Type determinado pela extensão do arquivo (evita depender do
    // mecanismo de detecção de MIME do sistema operacional, que pode variar).
    public String detectarContentType(String nomeArquivo) {
        String nome = nomeArquivo.toLowerCase();
        if (nome.endsWith(".png")) {
            return "image/png";
        }
        if (nome.endsWith(".jpg") || nome.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (nome.endsWith(".gif")) {
            return "image/gif";
        }
        if (nome.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
