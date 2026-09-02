package br.com.senai.produtosapi.exception;

/** Lançada ao cadastrar/atualizar um produto referenciando uma categoria com id inexistente. Vira HTTP 400. */
public class CategoriaInvalidaException extends RuntimeException {

    public CategoriaInvalidaException() {
        super("A categoria informada não existe. Verifique o id da categoria enviado.");
    }
}
