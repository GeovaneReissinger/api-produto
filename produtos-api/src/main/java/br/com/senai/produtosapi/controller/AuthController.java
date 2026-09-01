package br.com.senai.produtosapi.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.produtosapi.service.JwtService;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }
    @PostMapping
    public String login(@RequestBody LoginRequest login) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password()));

        return jwtService.gerarToken(auth.getName());

    }

}
