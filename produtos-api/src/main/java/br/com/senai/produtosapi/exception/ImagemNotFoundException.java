package br.com.senai.produtosapi.exception;

/** Lançada ao tentar baixar a imagem (GET /produtos/{id}/imagem) de um produto que não tem imagem cadastrada. Vira HTTP 404. */
public class ImagemNotFoundException extends RuntimeException {

    public ImagemNotFoundException(Long produtoId) {
        super("O produto com id " + produtoId + " não possui imagem cadastrada.");
    }
}
