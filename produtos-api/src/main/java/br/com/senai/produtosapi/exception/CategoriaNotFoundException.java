package br.com.senai.produtosapi.exception;

public class CategoriaNotFoundException extends RuntimeException {

    public CategoriaNotFoundException(Long id){
        super("Categoria não encontrada com id: " + id);
    }

}
