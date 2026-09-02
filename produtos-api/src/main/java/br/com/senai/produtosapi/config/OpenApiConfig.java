package br.com.senai.produtosapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configura a documentação Swagger/OpenAPI da API (título, versão e o esquema
 * de autenticação Bearer JWT usado pelo botão "Authorize" na tela do Swagger UI).
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI produtosApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Produtos API")
                        .description("API REST para gerenciamento de produtos e categorias.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "Faça login em POST /login para obter o token e clique em "
                                                + "\"Authorize\" informando apenas o token (o prefixo "
                                                + "'Bearer ' é adicionado automaticamente).")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
