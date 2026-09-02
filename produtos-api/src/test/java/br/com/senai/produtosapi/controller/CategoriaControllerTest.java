package br.com.senai.produtosapi.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import br.com.senai.produtosapi.IntegrationTestSupport;
import com.jayway.jsonpath.JsonPath;

/**
 * Testes de integração do CRUD de categorias (/categorias): listar, buscar por
 * id, cadastrar (válido e inválido), atualizar e remover, cobrindo tanto os
 * casos de sucesso quanto os de categoria inexistente.
 */
class CategoriaControllerTest extends IntegrationTestSupport {

    private String corpoCategoriaValida() {
        return """
                { "nome": "Categoria de teste %d" }
                """.formatted(System.nanoTime());
    }

    private Long criarCategoria() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/categorias")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoCategoriaValida()))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id");

        return id.longValue();
    }

    // Testa a listagem de todas as categorias.
    // A API deve processar a requisição com sucesso e retornar
    // HTTP 200 (OK).
    @Test
    void listarTodas_deveRetornar200() throws Exception {
        mockMvc.perform(
                get("/categorias")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk());
    }

    // Testa a busca de uma categoria existente pelo ID.
    // Primeiro cria uma categoria e depois consulta essa categoria.
    // A API deve retornar HTTP 200 e o ID informado na consulta.
    @Test
    void buscarPorId_existente_deveRetornar200() throws Exception {
        Long id = criarCategoria();

        mockMvc.perform(
                get("/categorias/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    // Testa a busca de uma categoria que não existe.
    // A API deve identificar que o recurso não foi encontrado
    // e retornar HTTP 404 (Not Found), juntamente com informações
    // sobre o erro.
    @Test
    void buscarPorId_inexistente_deveRetornar404ComCorpoDeErro() throws Exception {
        mockMvc.perform(
                get("/categorias/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    // Testa o cadastro de uma categoria utilizando dados válidos.
    // A API deve criar a categoria e retornar HTTP 201 (Created).
    // Também verifica se a categoria recebeu um ID.
    @Test
    void salvar_valido_deveRetornar201() throws Exception {
        mockMvc.perform(
                post("/categorias")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoCategoriaValida()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    // Testa o cadastro de uma categoria com o nome vazio.
    // Como o nome é obrigatório, a API deve rejeitar a requisição
    // e retornar HTTP 400 (Bad Request).
    @Test
    void salvar_nomeVazio_deveRetornar400() throws Exception {
        mockMvc.perform(
                post("/categorias")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"\" }"))
                .andExpect(status().isBadRequest());
    }

    // Testa a atualização de uma categoria existente.
    // Primeiro cria uma categoria e depois altera seu nome.
    // A API deve retornar HTTP 200 e o novo nome da categoria.
    @Test
    void atualizar_existente_deveRetornar200() throws Exception {
        Long id = criarCategoria();

        mockMvc.perform(
                put("/categorias/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"Categoria atualizada\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Categoria atualizada"));
    }

    // Testa a tentativa de atualizar uma categoria que não existe.
    // A API deve rejeitar a operação e retornar HTTP 404 (Not Found).
    @Test
    void atualizar_inexistente_deveRetornar404() throws Exception {
        mockMvc.perform(
                put("/categorias/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoCategoriaValida()))
                .andExpect(status().isNotFound());
    }

    // Testa a exclusão de uma categoria existente.
    // Primeiro cria a categoria e depois executa o DELETE.
    // A API deve retornar HTTP 204 (No Content).
    // Em seguida, consulta a mesma categoria para confirmar
    // que ela foi realmente removida e retorna HTTP 404.
    @Test
    void deletar_existente_deveRetornar204EDepoisNotFound() throws Exception {
        Long id = criarCategoria();

        mockMvc.perform(
                delete("/categorias/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/categorias/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound());
    }

    // Testa a tentativa de excluir uma categoria que não existe.
    // A API deve identificar que o recurso não foi encontrado
    // e retornar HTTP 404 (Not Found).
    @Test
    void deletar_inexistente_deveRetornar404() throws Exception {
        mockMvc.perform(
                delete("/categorias/{id}", 999_999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(status().isNotFound());
    }
}