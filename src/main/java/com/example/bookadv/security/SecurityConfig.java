package com.example.bookadv.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Proveedor de autenticación
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests(auth -> auth
            // públicos
            .requestMatchers("/", "/public/**", "/libros", "/generos", "/login**", "/signup**", "/logout-success").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()

            // usuario autenticado puede ver su perfil y cambiar contraseña
            .requestMatchers("/usuarios/editar-perfil", "/usuarios/cambiar-password")
                .authenticated()

            // USER+ para crear valoraciones, pero backend filtra borrado
            .requestMatchers("/valoraciones/nueva", "/valoraciones/save", "/valoraciones/libro/**")
                .hasAnyRole("USER","MANAGER","ADMIN")

            // MANAGER+ para CRUD de libros, géneros y valoraciones
            .requestMatchers("/valoraciones/**", "/libros/**", "/generos/**")
                .hasAnyRole("MANAGER","ADMIN")

            // ADMIN para CRUD usuarios
            .requestMatchers("/usuarios/**")
                .hasRole("ADMIN")

            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/public/", true)
            .failureUrl("/login?error")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            // apuntas ahora a /logout-success
            .logoutSuccessUrl("/logout-success")
            .permitAll()
        )
        .exceptionHandling(ex -> ex
            .accessDeniedPage("/accessError")
        )
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
