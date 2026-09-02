package br.com.senai.produtosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.produtosapi.model.Categoria;

/** Acesso a dados de {@link Categoria}. CRUD básico herdado de {@link JpaRepository}, sem consultas extras. */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
