package br.com.senai.produtosapi.controller;

import jakarta.validation.constraints.NotBlank;

/** Corpo esperado pelo POST /login: usuário e senha, ambos obrigatórios. */
public record LoginRequest(
        @NotBlank(message = "O usuário é obrigatório.") String username,
        @NotBlank(message = "A senha é obrigatória.") String password) {
}
