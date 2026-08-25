package br.com.senai.produtosapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI produtosApiOpenAPI(){

        return new OpenAPI()
                .info(new Info()
                            .title("Produtos API")
                            .description("API REST para gerenciamento de produtos e categorias.")
                            .version("v1"));
        
    }
    
}
