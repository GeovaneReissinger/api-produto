package br.com.senai.produtosapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe de entrada da aplicação Spring Boot.
// @SpringBootApplication junta 3 anotações em uma só:
// - @Configuration: permite declarar beans nesta classe
// - @EnableAutoConfiguration: o Spring configura automaticamente o que for
//   necessário (servidor web, JPA, etc.) com base nas dependências do projeto
// - @ComponentScan: faz o Spring procurar por @Controller, @Service e
//   @Repository dentro deste pacote e subpacotes (controller, service, repository)
@SpringBootApplication
public class ProdutosApiApplication {

	// Ponto de partida do programa: sobe o servidor embutido (Tomcat)
	// e inicializa todo o contexto do Spring (injeção de dependência).
	public static void main(String[] args) {
		SpringApplication.run(ProdutosApiApplication.class, args);
	}

}
