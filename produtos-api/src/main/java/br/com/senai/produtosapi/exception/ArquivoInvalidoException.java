package br.com.senai.produtosapi.exception;

/** Lançada quando o arquivo de imagem enviado está vazio ou tem um tipo MIME não aceito. Vira HTTP 400. */
public class ArquivoInvalidoException extends RuntimeException {

    public ArquivoInvalidoException(String message) {
        super(message);
    }
}
