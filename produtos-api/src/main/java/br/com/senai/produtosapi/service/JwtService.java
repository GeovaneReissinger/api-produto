package br.com.senai.produtosapi.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Gera e valida os tokens JWT usados para autenticação: assina o token com uma
 * chave secreta (HMAC), define a validade de 1 hora e permite extrair o username
 * de um token já emitido. Usado no login ({@link br.com.senai.produtosapi.controller.AuthController})
 * e na validação de cada requisição ({@link br.com.senai.produtosapi.security.JwtAuthFilter}).
 */
@Service
public class JwtService {

    // A chave vem de application.properties (jwt.secret), que por sua vez lê a
    // variável de ambiente JWT_SECRET. Em produção, JWT_SECRET deve ser configurada
    // externamente (nunca versionada); o valor default existe só para uso local/didático.
    private final String secret;
    private static final long VALIDADE_MS = 1000 * 60 * 60; // 1 hora

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    private SecretKey getChave() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String gerarToken(String username) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + VALIDADE_MS);

        return Jwts.builder()
                .subject(username)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChave())
                .compact();
    }

    public String extrairUsername(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getChave())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
