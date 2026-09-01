package br.com.senai.produtosapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.senai.produtosapi.repository.UsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails usuario = usuarioRepository.findByUsername(username);
        if(usuario == null){
            throw new UsernameNotFoundException("Usuário não encntrado: " + username);
        }        
        return usuario;
    }   
    
}
