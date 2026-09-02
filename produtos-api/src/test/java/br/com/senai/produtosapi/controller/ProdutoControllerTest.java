package br.com.senai.produtosapi.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import br.com.senai.produtosapi.IntegrationTestSupport;
import br.com.senai.produtosapi.model.Categoria;
import br.com.senai.produtosapi.repository.CategoriaRepository;
import com.jayway.jsonpath.JsonPath;

/**
 * Testes de integração do CRUD de produtos (/produtos): listar, buscar por id,
 * cadastrar (com validações de nome, preço e categoria), atualizar, remover e
 * checar que o acesso sem JWT é bloqueado. Cada teste cria sua própria categoria de apoio.
 */
class ProdutoControllerTest extends IntegrationTestSupport {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Long categoriaId;

    @BeforeEach
    void criarCategoriaDeApoio() {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria de teste " + System.nanoTime());
        categoriaId = categoriaRepository.save(categoria).getId();
    }

    private String corpoProdutoValido() {
        return """
                {
                    "nome": "Produto de teste",
                    "descricao": "Descrição do produto de teste",
                    "preco": 99.90,
                    "categoria": { "id": %d }
                }
                """.formatted(categoriaId);
    }

    private Long criarProduto() throws Exception {
        MvcResult result = mockMvc.perform(post("/produtos")
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoProdutoValido()))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id");

        return id.longValue();
    }

    // Testa se a API consegue listar todos os produtos
    // e retorna o status HTTP 200 (OK).
    @Test
    void listarTodos_deveRetornar200() throws Exception {
        mockMvc.perform(
                get("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk());
    }

    // Testa a busca de um produto existente pelo seu ID.
    // Primeiro cria um produto e depois verifica se a API
    // retorna o status HTTP 200 e o ID correto.
    @Test
    void buscarPorId_existente_deveRetornar200() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(
                get("/produtos/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    // Testa a busca de um produto que não existe.
    // A API deve retornar HTTP 404 (Not Found) e um corpo
    // contendo informações sobre o erro.
    @Test
    void buscarPorId_inexistente_deveRetornar404ComCorpoDeErro() throws Exception {
        mockMvc.perform(
                get("/produtos/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // Testa o cadastro de um produto com dados válidos.
    // A API deve criar o produto e retornar HTTP 201 (Created).
    // Também verifica se o produto recebeu um ID e se o nome
    // retornado está correto.
    @Test
    void salvar_valido_deveRetornar201() throws Exception {
        mockMvc.perform(
                post("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoProdutoValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome").value("Produto de teste"));
    }

    // Testa o cadastro de um produto com o campo "nome" vazio.
    // Como o nome é obrigatório, a API deve rejeitar a requisição
    // e retornar HTTP 400 (Bad Request).
    @Test
    void salvar_nomeVazio_deveRetornar400() throws Exception {
        String corpo = """
                {
                    "nome": "",
                    "preco": 10.00,
                    "categoria": { "id": %d }
                }
                """.formatted(categoriaId);

        mockMvc.perform(
                post("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    // Testa o cadastro de um produto sem informar o preço.
    // Como o preço é obrigatório, a API deve retornar HTTP 400
    // indicando que os dados enviados são inválidos.
    @Test
    void salvar_precoNulo_deveRetornar400() throws Exception {
        String corpo = """
                {
                    "nome": "Produto sem preço",
                    "categoria": { "id": %d }
                }
                """.formatted(categoriaId);

        mockMvc.perform(
                post("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    // Testa o cadastro de um produto com preço negativo.
    // A API deve validar o valor informado e retornar HTTP 400,
    // pois o preço não pode ser negativo.
    @Test
    void salvar_precoNegativo_deveRetornar400() throws Exception {
        String corpo = """
                {
                    "nome": "Produto com preço negativo",
                    "preco": -10.00,
                    "categoria": { "id": %d }
                }
                """.formatted(categoriaId);

        mockMvc.perform(
                post("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    // Testa o cadastro de um produto informando uma categoria
    // que não existe no banco de dados.
    // A API deve rejeitar a operação e retornar HTTP 400.
    @Test
    void salvar_categoriaInexistente_deveRetornar400() throws Exception {
        String corpo = """
                {
                    "nome": "Produto com categoria inexistente",
                    "preco": 10.00,
                    "categoria": { "id": 999999999 }
                }
                """;

        mockMvc.perform(
                post("/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    // Testa a atualização de um produto existente.
    // Primeiro cria um produto, depois altera seus dados.
    // A API deve retornar HTTP 200 (OK) e o nome atualizado.
    @Test
    void atualizar_existente_deveRetornar200() throws Exception {
        Long id = criarProduto();

        String corpo = """
                {
                    "nome": "Produto atualizado",
                    "preco": 150.00,
                    "categoria": { "id": %d }
                }
                """.formatted(categoriaId);

        mockMvc.perform(
                put("/produtos/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto atualizado"));
    }

    // Testa a tentativa de atualizar um produto que não existe.
    // A API deve identificar que o ID informado não foi encontrado
    // e retornar HTTP 404 (Not Found).
    @Test
    void atualizar_inexistente_deveRetornar404() throws Exception {
        String corpo = corpoProdutoValido();

        mockMvc.perform(
                put("/produtos/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isNotFound());
    }

    // Testa a exclusão de um produto existente.
    // Primeiro cria o produto, depois realiza o DELETE.
    // A API deve retornar HTTP 204 (No Content).
    // Em seguida, tenta buscar o mesmo produto para confirmar
    // que ele realmente foi removido e retorna HTTP 404.
    @Test
    void deletar_existente_deveRetornar204EDepoisNotFound() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(
                delete("/produtos/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/produtos/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound());
    }

    // Testa a tentativa de excluir um produto que não existe.
    // A API deve retornar HTTP 404 (Not Found).
    @Test
    void deletar_inexistente_deveRetornar404() throws Exception {
        mockMvc.perform(
                delete("/produtos/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound());
    }

    // Testa o acesso ao endpoint sem enviar o token JWT.
    // Como o endpoint exige autenticação, a API deve bloquear
    // a requisição e retornar HTTP 401 (Unauthorized).
    @Test
    void listarTodos_semJwt_deveRetornar401() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isUnauthorized());
    }
}