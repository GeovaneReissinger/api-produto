package br.com.senai.produtosapi.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import br.com.senai.produtosapi.IntegrationTestSupport;
import br.com.senai.produtosapi.model.Categoria;
import br.com.senai.produtosapi.repository.CategoriaRepository;
import com.jayway.jsonpath.JsonPath;

/**
 * Testes de integração do upload e download de imagem de produto
 * (POST/GET /produtos/{id}/imagem): upload válido, produto inexistente, tipo de
 * arquivo inválido, arquivo vazio e download com o Content-Type correto.
 * Os arquivos gerados durante os testes são apagados da pasta uploads ao final de cada teste.
 */
class ProdutoImagemControllerTest extends IntegrationTestSupport {

        @Autowired
        private CategoriaRepository categoriaRepository;

        private Long categoriaId;

        private final List<String> arquivosCriados = new ArrayList<>();

        // Cria uma categoria de apoio antes de cada teste.
        // Essa categoria será utilizada para criar os produtos necessários
        // para os cenários de upload e download de imagens.
        @BeforeEach
        void criarCategoriaDeApoio() {
                Categoria categoria = new Categoria();
                categoria.setNome("Categoria imagem " + System.nanoTime());
                categoriaId = categoriaRepository.save(categoria).getId();
        }

        // Remove os arquivos criados durante os testes.
        // Essa limpeza evita que as imagens utilizadas pelos testes
        // permaneçam na pasta "uploads" após a execução da suíte.
        @AfterEach
        void limparArquivosCriados() throws IOException {
                for (String nome : arquivosCriados) {
                        Files.deleteIfExists(Paths.get("uploads").resolve(nome));
                }
        }

        // Cria um produto de apoio para ser utilizado nos testes
        // de upload e download de imagens.
        // Retorna o ID do produto criado para utilização nos testes.
        private Long criarProduto() throws Exception {
                String corpo = """
                                {
                                    "nome": "Produto com imagem",
                                    "preco": 50.00,
                                    "categoria": { "id": %d }
                                }
                                """.formatted(categoriaId);

                MvcResult result = mockMvc.perform(
                                post("/produtos")
                                                .header(HttpHeaders.AUTHORIZATION, bearer())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpo))
                                .andExpect(status().isCreated())
                                .andReturn();

                Number id = JsonPath.read(
                                result.getResponse().getContentAsString(),
                                "$.id");

                return id.longValue();
        }

        // Testa o upload de uma imagem para um produto existente.
        // A requisição deve ser aceita e retornar HTTP 200 (OK).
        // Também verifica se a resposta contém o nome da imagem armazenada.
        @Test
        void upload_valido_deveRetornar200EAtualizarImagem() throws Exception {
                Long id = criarProduto();

                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "foto.jpg",
                                "image/jpeg",
                                "conteudo-fake".getBytes());

                MvcResult result = mockMvc.perform(
                                multipart("/produtos/{id}/imagem", id)
                                                .file(arquivo)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.imagem", notNullValue()))
                                .andReturn();

                String nomeArquivo = JsonPath.read(
                                result.getResponse().getContentAsString(),
                                "$.imagem");

                arquivosCriados.add(nomeArquivo);
        }

        // Testa o upload de uma imagem informando um produto que não existe.
        // A API deve identificar que o produto não foi encontrado
        // e retornar HTTP 404 (Not Found).
        @Test
        void upload_produtoInexistente_deveRetornar404() throws Exception {
                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "foto.jpg",
                                "image/jpeg",
                                "conteudo-fake".getBytes());

                mockMvc.perform(
                                multipart("/produtos/{id}/imagem", 999_999_999L)
                                                .file(arquivo)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isNotFound());
        }

        // Testa o upload de um arquivo com tipo MIME não permitido.
        // Neste cenário, um arquivo PDF é enviado em vez de uma imagem.
        // A API deve rejeitar o arquivo e retornar HTTP 400 (Bad Request).
        @Test
        void upload_tipoInvalido_deveRetornar400() throws Exception {
                Long id = criarProduto();

                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "documento.pdf",
                                "application/pdf",
                                "conteudo".getBytes());

                mockMvc.perform(
                                multipart("/produtos/{id}/imagem", id)
                                                .file(arquivo)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isBadRequest());
        }

        // Testa o upload de um arquivo vazio.
        // Como não existe conteúdo no arquivo enviado, a API deve rejeitar
        // a requisição e retornar HTTP 400 (Bad Request).
        @Test
        void upload_arquivoVazio_deveRetornar400() throws Exception {
                Long id = criarProduto();

                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "vazio.jpg",
                                "image/jpeg",
                                new byte[0]);

                mockMvc.perform(
                                multipart("/produtos/{id}/imagem", id)
                                                .file(arquivo)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isBadRequest());
        }

        // Testa o download de uma imagem que foi previamente enviada
        // para um produto existente.
        // A API deve retornar HTTP 200, o Content-Type correspondente
        // ao formato da imagem e o Content-Disposition como "inline".
        @Test
        void download_existente_deveRetornarContentTypeCorreto() throws Exception {
                Long id = criarProduto();

                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "foto.png",
                                "image/png",
                                "conteudo-fake".getBytes());

                MvcResult uploadResult = mockMvc.perform(
                                multipart("/produtos/{id}/imagem", id)
                                                .file(arquivo)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isOk())
                                .andReturn();

                String nomeArquivo = JsonPath.read(
                                uploadResult.getResponse().getContentAsString(),
                                "$.imagem");

                arquivosCriados.add(nomeArquivo);

                mockMvc.perform(
                                get("/produtos/{id}/imagem", id)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isOk())
                                .andExpect(header().string(
                                                HttpHeaders.CONTENT_TYPE,
                                                "image/png"))
                                .andExpect(header().string(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                containsString("inline")));
        }

        // Testa o download de uma imagem para um produto que não possui imagem.
        // Como não existe nenhum arquivo associado ao produto, a API deve
        // retornar HTTP 404 (Not Found).
        @Test
        void download_semImagem_deveRetornar404() throws Exception {
                Long id = criarProduto();

                mockMvc.perform(
                                get("/produtos/{id}/imagem", id)
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isNotFound());
        }
}