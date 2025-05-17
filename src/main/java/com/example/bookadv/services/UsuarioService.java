package com.example.bookadv.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bookadv.DTO.ChangePasswordDto;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Guardar usuario, ya sea nuevo o modificado
    public Usuario save(Usuario usuario) throws Exception {
        // Validar nombre de usuario único (si es nuevo o se modifica el nombre)
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNombre(usuario.getNombre());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
            throw new Exception("El nombre de usuario ya está en uso");
        }

        // Encriptar la contraseña antes de guardar, si la contraseña no está vacía
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);
        } else if (usuario.getId() != null) {
            // Si es edición y no se cambia la contraseña, mantenemos la anterior
            Usuario usuarioDB = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
            usuario.setPassword(usuarioDB.getPassword());
        } else {
            throw new Exception("La contraseña es obligatoria");
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Método para obtener usuario por nombre
    public Optional<Usuario> findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    /**
     * Cambia la contraseña de un usuario validando la actual y que new == confirm.
     */
    public void changePassword(String username, ChangePasswordDto dto) {
        Usuario u = usuarioRepository.findByNombre(username)
            .orElseThrow();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), u.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no coincide");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las nuevas contraseñas no coinciden");
        }
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(u);
    }


    public void updateProfile (String currentUsername, Usuario form) {
        Usuario u = findByNombre(currentUsername)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Usuario no encontrado con nombre: " + currentUsername
        ));
        u.setNombre(form.getNombre());
        usuarioRepository.save(u);
    }
}