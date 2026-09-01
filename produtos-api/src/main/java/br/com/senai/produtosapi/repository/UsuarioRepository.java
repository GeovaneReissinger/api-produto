package br.com.senai.produtosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.produtosapi.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    Usuario findByUsername(String username);
    
}
