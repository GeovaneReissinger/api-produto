package br.com.senai.produtosapi.exception;

/** Lançada quando ocorre uma falha de I/O ao salvar um arquivo de imagem em disco. Vira HTTP 500. */
public class ArmazenamentoException extends RuntimeException {

    public ArmazenamentoException(String message, Throwable causa) {
        super(message, causa);
    }
}
