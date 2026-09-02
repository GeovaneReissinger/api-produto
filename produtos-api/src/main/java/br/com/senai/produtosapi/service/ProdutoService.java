package br.com.senai.produtosapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.senai.produtosapi.exception.CategoriaInvalidaException;
import br.com.senai.produtosapi.exception.ProdutoNotFoundException;
import br.com.senai.produtosapi.model.Produto;
import br.com.senai.produtosapi.repository.CategoriaRepository;
import br.com.senai.produtosapi.repository.ProdutoRepository;

/**
 * Regras de negócio de produto: CRUD, buscas por categoria/faixa de preço e
 * associação da imagem enviada. Valida que a categoria informada existe antes
 * de salvar ou atualizar um produto, lançando {@link CategoriaInvalidaException} caso contrário.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
    }

    public Produto salvar(Produto produto) {
        validarCategoria(produto);
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        validarCategoria(produtoAtualizado);
        Produto produto = buscarPorId(id);
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setCategoria(produtoAtualizado.getCategoria());
        return produtoRepository.save(produto);
    }

    private void validarCategoria(Produto produto) {
        Long categoriaId = produto.getCategoria() != null ? produto.getCategoria().getId() : null;
        if (categoriaId == null || !categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaInvalidaException();
        }
    }

    public void deletar(Long id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorCategoria(Long categoriaId) {
        return produtoRepository.buscarPorCategoria(categoriaId);
    }

    public List<Produto> buscarPorFaixaDePreco(BigDecimal min, BigDecimal max) {
        return produtoRepository.buscarPorFaixaDePreco(min, max);
    }

    public Produto atualizarImagem(Long id, String nomeArquivo) {
        Produto produto = buscarPorId(id);
        produto.setImagem(nomeArquivo);
        return produtoRepository.save(produto);
    }
}
