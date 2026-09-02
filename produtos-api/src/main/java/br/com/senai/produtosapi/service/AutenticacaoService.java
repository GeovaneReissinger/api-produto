package br.com.senai.produtosapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.senai.produtosapi.repository.UsuarioRepository;

/**
 * Ponte entre o Spring Security e o banco de usuários: dado um username, busca
 * o {@link br.com.senai.produtosapi.model.Usuario} correspondente para que o
 * Spring possa validar a senha e montar a autenticação (usado no login e na validação do JWT).
 */
@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return usuario;
    }
}
