package br.com.senai.produtosapi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * Ponto único que intercepta as exceções lançadas pelos controllers e services
 * e as converte em respostas HTTP padronizadas (status + {@link ErrorResponse}),
 * em vez de deixar o Spring devolver uma stack trace crua para o cliente.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProdutoNotFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CategoriaNotFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ImagemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ImagemNotFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ArquivoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleArquivoInvalido(ArquivoInvalidoException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CategoriaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaInvalida(CategoriaInvalidaException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({ MissingServletRequestPartException.class, MultipartException.class })
    public ResponseEntity<ErrorResponse> handleArquivoAusente(Exception ex) {
        return construir(HttpStatus.BAD_REQUEST, "Arquivo de imagem não enviado ou requisição multipart inválida.");
    }

    @ExceptionHandler(ArmazenamentoException.class)
    public ResponseEntity<ErrorResponse> handleArmazenamento(ArmazenamentoException ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao armazenar o arquivo de imagem.");
    }

    // Cobre tanto usuário inexistente quanto senha incorreta: por segurança, o
    // Spring Security oculta qual dos dois casos ocorreu (evita enumeração de usuários).
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "Falha na autenticação.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));

        ErrorResponse corpo = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos dados enviados.",
                LocalDateTime.now(),
                erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    // Último recurso: evita expor stack trace ou detalhes internos ao cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensagem) {
        ErrorResponse erro = new ErrorResponse(status.value(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(erro);
    }
}
