package br.com.senai.produtosapi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProdutoNotFoundException ex) {
        ErrorResponse erro = new ErrorResponse(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CategoriaNotFoundException ex) {
        ErrorResponse erro = new ErrorResponse(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex){

        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult()
        .getFieldErrors()
        .forEach(erro -> erros.put(
            erro.getField(),
            erro.getDefaultMessage()));

            return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(erros);
    }

}
