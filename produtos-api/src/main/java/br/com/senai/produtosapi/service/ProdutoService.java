package br.com.senai.produtosapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.senai.produtosapi.exception.ProdutoNotFoundException;
import br.com.senai.produtosapi.model.Produto;
import br.com.senai.produtosapi.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setCategoria(produtoAtualizado.getCategoria());
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorCategoria(Long categoriaId){
        return produtoRepository.buscarPorCategoria(categoriaId);
    }

    public List<Produto> buscarPorFaixaDePreco(BigDecimal min, BigDecimal max){
        return produtoRepository.buscarPorFaixaDePreco(min, max);

    }
}
