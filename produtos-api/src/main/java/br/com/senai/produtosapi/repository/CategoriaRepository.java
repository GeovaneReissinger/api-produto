package br.com.senai.produtosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.produtosapi.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
}
