package br.com.senai.produtosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.produtosapi.model.Usuario;

/**
 * Acesso a dados de {@link Usuario}. O método findByUsername é usado pelo
 * {@link br.com.senai.produtosapi.service.AutenticacaoService} durante o login.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);
}
