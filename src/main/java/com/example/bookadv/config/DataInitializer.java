package com.example.bookadv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookadv.domain.Role;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.repository.UsuarioRepository;

@Configuration
public class DataInitializer implements CommandLineRunner{
    
    @Autowired
    private final UsuarioRepository usuarioRepository;
    
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String...args) throws Exception {
        String adminUsername = "admin";
        //Si no existe el usuario lo creamos.
        if (usuarioRepository.findByNombre(adminUsername).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre(adminUsername);
            // encriptamos la contraseña '1234'
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRol(Role.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN por defecto creado: admin / 1234");
        }
    }
}
