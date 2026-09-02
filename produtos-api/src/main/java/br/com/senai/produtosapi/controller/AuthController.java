package br.com.senai.produtosapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.produtosapi.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Endpoint de login (POST /login). Recebe usuário e senha, delega a autenticação
 * ao Spring Security e, se as credenciais forem válidas, devolve um token JWT
 * que deve ser enviado nas próximas requisições em "Authorization: Bearer &lt;token&gt;".
 */
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Autenticar usuário e gerar token JWT",
            description = "Retorna um token JWT que deve ser enviado no header 'Authorization: Bearer <token>' "
                    + "nas requisições aos endpoints protegidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso, token JWT retornado"),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    })
    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest login) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password()));

        return ResponseEntity.ok(jwtService.gerarToken(auth.getName()));
    }
}
