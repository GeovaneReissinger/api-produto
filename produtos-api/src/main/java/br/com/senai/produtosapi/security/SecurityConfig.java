package br.com.senai.produtosapi.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração central de segurança da API: define que a aplicação é stateless
 * (autenticação via JWT, sem sessão), quais endpoints são públicos ou exigem
 * login, o CORS liberado para o frontend local e o encoder de senha (BCrypt).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
     * Filtro responsável por interceptar as requisições e verificar
     * se existe um JWT válido no cabeçalho Authorization.
     */
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /*
     * Define o mecanismo utilizado para criptografar as senhas
     * armazenadas na aplicação.
     *
     * O BCrypt gera um hash seguro que não permite recuperar
     * diretamente a senha original.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Gerenciador responsável pelo processo de autenticação
     * dos usuários.
     *
     * É utilizado, por exemplo, durante o login para verificar
     * usuário e senha.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /*
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * Aqui configuramos:
     * - CSRF
     * - CORS
     * - Sessões
     * - Tratamento de erros de autenticação
     * - Endpoints públicos e protegidos
     * - Filtro responsável pelo JWT
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                /*
                 * Desabilita o CSRF.
                 *
                 * Neste projeto estamos utilizando uma API REST
                 * stateless com autenticação baseada em JWT.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * Habilita o CORS e utiliza a configuração definida
                 * no método corsConfigurationSource().
                 *
                 * Isso permite que um frontend executado em outra
                 * origem, como http://localhost:5500, acesse a API.
                 */
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))

                /*
                 * Define a aplicação como STATELESS.
                 *
                 * Isso significa que o servidor não mantém uma
                 * sessão HTTP para cada usuário.
                 *
                 * A autenticação é feita através do JWT enviado
                 * em cada requisição.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                /*
                 * Quando uma requisição não autenticada tenta
                 * acessar um endpoint protegido, a API retorna
                 * HTTP 401 - Unauthorized.
                 */
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(
                                HttpStatus.UNAUTHORIZED)))

                /*
                 * Define quais endpoints podem ser acessados
                 * sem autenticação.
                 *
                 * /login é público porque o usuário precisa
                 * conseguir fazer login antes de possuir um JWT.
                 *
                 * O Swagger também é liberado para permitir
                 * a documentação e os testes da API.
                 */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos/*/imagem")
                        .permitAll()

                        /*
                         * Qualquer outro endpoint exige autenticação.
                         */
                        .anyRequest().authenticated())

                /*
                 * Adiciona o filtro JWT antes do filtro padrão
                 * de autenticação do Spring Security.
                 *
                 * O JwtAuthFilter verifica o token enviado
                 * no cabeçalho Authorization.
                 */
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Configuração do CORS.
     *
     * O CORS controla quais aplicações externas podem
     * realizar requisições para esta API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Permite o acesso dos frontends executados
         * localmente através do Live Server.
         *
         * Foram adicionadas as duas possibilidades:
         * localhost e 127.0.0.1.
         */
        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"));

        /*
         * Define os métodos HTTP permitidos pelo frontend.
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"));

        /*
         * Permite que o frontend envie os cabeçalhos necessários,
         * incluindo o Authorization utilizado pelo JWT.
         */
        configuration.setAllowedHeaders(List.of("*"));

        /*
         * Permite o envio de credenciais nas requisições.
         */
        configuration.setAllowCredentials(true);

        /*
         * Aplica essa configuração de CORS para todos os endpoints
         * da API.
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration);

        return source;
    }
}
