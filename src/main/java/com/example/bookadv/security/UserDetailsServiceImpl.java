package com.example.bookadv.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bookadv.domain.Usuario;
import com.example.bookadv.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombre(nombre)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre: " + nombre));

        // Creamos un UserDetails con username, password y roles (Spring Security usa prefijo "ROLE_")
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

        return new User(
            usuario.getNombre(),
            usuario.getPassword(),
            Collections.singletonList(autoridad)
        );
    }
}
