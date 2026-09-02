package br.com.senai.produtosapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Formato padrão de corpo de erro devolvido pela API (usado pelo
 * {@link GlobalExceptionHandler}). O campo "errors" só aparece no JSON quando
 * não é nulo, ou seja, apenas em erros de validação de campos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors) {

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}
