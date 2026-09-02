package br.com.senai.produtosapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import br.com.senai.produtosapi.IntegrationTestSupport;

/**
 * Testes de integração do login (POST /login) e da proteção por JWT dos
 * endpoints: login válido/inválido, senha errada e acesso a um endpoint
 * protegido sem token, com token válido e com token inválido.
 */
class AuthControllerTest extends IntegrationTestSupport {

        // Testa o login utilizando um usuário e uma senha válidos.
        // A API deve autenticar o usuário e retornar HTTP 200 (OK),
        // indicando que o login foi realizado com sucesso e um token JWT
        // foi gerado.
        @Test
        void login_valido_deveRetornar200ComToken() throws Exception {
                String corpo = """
                                { "username": "%s", "password": "%s" }
                                """.formatted(usernameTeste, SENHA_TESTE);

                mockMvc.perform(
                                post("/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpo))
                                .andExpect(status().isOk());
        }

        // Testa o login utilizando um usuário que não existe no sistema.
        // Como não é possível autenticar um usuário inexistente,
        // a API deve retornar HTTP 401 (Unauthorized).
        @Test
        void login_usuarioInexistente_deveRetornar401() throws Exception {
                String corpo = """
                                { "username": "usuario-que-nao-existe", "password": "qualquer" }
                                """;

                mockMvc.perform(
                                post("/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpo))
                                .andExpect(status().isUnauthorized());
        }

        // Testa o login utilizando um usuário existente, mas com uma
        // senha incorreta.
        // A autenticação deve ser recusada e a API deve retornar
        // HTTP 401 (Unauthorized).
        @Test
        void login_senhaInvalida_deveRetornar401() throws Exception {
                String corpo = """
                                { "username": "%s", "password": "senha-errada" }
                                """.formatted(usernameTeste);

                mockMvc.perform(
                                post("/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpo))
                                .andExpect(status().isUnauthorized());
        }

        // Testa o acesso a um endpoint protegido sem enviar o token JWT.
        // Como o endpoint /produtos exige autenticação, a API deve bloquear
        // a requisição e retornar HTTP 401 (Unauthorized).
        @Test
        void endpointProtegido_semJwt_deveRetornar401() throws Exception {
                mockMvc.perform(get("/produtos"))
                                .andExpect(status().isUnauthorized());
        }

        // Testa o acesso a um endpoint protegido enviando um token JWT válido.
        // Como o usuário está autenticado corretamente, a API deve permitir
        // o acesso e retornar HTTP 200 (OK).
        @Test
        void endpointProtegido_comJwtValido_deveRetornar200() throws Exception {
                mockMvc.perform(
                                get("/produtos")
                                                .header(HttpHeaders.AUTHORIZATION, bearer()))
                                .andExpect(status().isOk());
        }

        // Testa o acesso a um endpoint protegido utilizando um token JWT inválido.
        // Como o token não pode ser validado pela aplicação, a requisição
        // deve ser rejeitada e retornar HTTP 401 (Unauthorized).
        @Test
        void endpointProtegido_comJwtInvalido_deveRetornar401() throws Exception {
                mockMvc.perform(
                                get("/produtos")
                                                .header(
                                                                HttpHeaders.AUTHORIZATION,
                                                                "Bearer token-invalido"))
                                .andExpect(status().isUnauthorized());
        }
}