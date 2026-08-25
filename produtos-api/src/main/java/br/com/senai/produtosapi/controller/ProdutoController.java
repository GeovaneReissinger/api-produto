package br.com.senai.produtosapi.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.produtosapi.model.Produto;
import br.com.senai.produtosapi.service.ProdutoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")

public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // GET /produtos -> lista todos os produtos cadastrados
    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    // GET /produtos/{id} -> busca um produto específico
    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    // POST /produtos -> Cria um produto
    @PostMapping
    public Produto salvar(@Valid @RequestBody Produto produto) {
        return produtoService.salvar(produto);
    }

    // PUT /produtos/{id} -> atualiza um produto existente
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return produtoService.atualizar(id, produto);
    }

    // DELETE /produtos/{id} -> Remove um produto pelo id
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        produtoService.deletar(id);
    }

    
    @GetMapping("/categoria/{categoriaId}")
    public List<Produto> buscarPorCategoria(@PathVariable Long categoriaId) {
        return produtoService.buscarPorCategoria(categoriaId);
    }

    @GetMapping("/faixa-preco")
    public List<Produto> buscarPorFaixaDePreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return produtoService.buscarPorFaixaDePreco(min, max);
    }

}
