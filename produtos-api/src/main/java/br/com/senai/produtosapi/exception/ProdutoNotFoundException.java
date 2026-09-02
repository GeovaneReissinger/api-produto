package br.com.senai.produtosapi.exception;

/** Lançada quando o id de produto informado (GET/PUT/DELETE /produtos/{id}) não existe. Vira HTTP 404. */
public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(Long id) {
        super("Produto não encontrado com id: " + id);
    }
}
