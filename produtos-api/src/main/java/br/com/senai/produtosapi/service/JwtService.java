package br.com.senai.produtosapi.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Em produção, essa chave deve vir de uma variável de ambiente, nunca do código.
    private static final String SECRET = "ZmlyamFuLXNlbmFpLXByb2dyYW1hZG9yLWJhY2tlbmQtand0LXNlY3JldA==";
    private static final long VALIDADE_MS = 1000 * 60 * 60; // 1 hora

    private SecretKey getChave() {
        byte[] bytes = Decoders.BASE64.decode(SECRET);
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