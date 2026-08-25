package br.com.senai.produtosapi.exception;

public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(Long id){
        super("Produto não encontrado com id: " + id);
    }
    
}
