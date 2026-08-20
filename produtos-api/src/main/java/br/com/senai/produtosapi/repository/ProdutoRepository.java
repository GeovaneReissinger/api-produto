package br.com.senai.produtosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.senai.produtosapi.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
}