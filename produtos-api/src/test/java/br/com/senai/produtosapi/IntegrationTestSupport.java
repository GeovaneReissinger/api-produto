package br.com.senai.produtosapi;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.senai.produtosapi.model.Usuario;
import br.com.senai.produtosapi.repository.UsuarioRepository;
import br.com.senai.produtosapi.service.JwtService;

/**
 * Base para testes de integração: sobe o contexto Spring completo contra o banco
 * PostgreSQL configurado em application.properties e envolve cada teste em uma
 * transação que é revertida ao final (não deixa dados residuais no banco usado
 * pelas aulas).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    protected String usernameTeste;
    protected static final String SENHA_TESTE = "senha123";
    protected String tokenValido;

    @BeforeEach
    void criarUsuarioDeTeste() {
        usernameTeste = "usuario.teste." + UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameTeste);
        usuario.setPassword(passwordEncoder.encode(SENHA_TESTE));
        usuario.setRole("USER");
        usuarioRepository.save(usuario);

        tokenValido = jwtService.gerarToken(usernameTeste);
    }

    protected String bearer() {
        return "Bearer " + tokenValido;
    }
}
