package br.com.senai.produtosapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path pasta = Paths.get("uploads");

    public String salvar(MultipartFile arquivo) throws IOException{

        Files.createDirectories(pasta);

        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();

        Path destino = pasta.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), destino);
        return nomeArquivo;
    }
    
}
