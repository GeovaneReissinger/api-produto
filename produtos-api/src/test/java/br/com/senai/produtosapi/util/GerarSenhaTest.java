package br.com.senai.produtosapi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarSenhaTest {

    @org.junit.jupiter.api.Test
    public void gerarHash(){
        String senha = System.getProperty("senha", "senha123");
        System.out.println(senha + " -> " + new BCryptPasswordEncoder().encode(senha));
    }
    
}
