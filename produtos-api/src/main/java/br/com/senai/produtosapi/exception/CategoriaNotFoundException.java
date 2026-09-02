package br.com.senai.produtosapi.exception;

/** Lançada quando o id de categoria informado (GET/PUT/DELETE /categorias/{id}) não existe. Vira HTTP 404. */
public class CategoriaNotFoundException extends RuntimeException {

    public CategoriaNotFoundException(Long id) {
        super("Categoria não encontrada com id: " + id);
    }
}
