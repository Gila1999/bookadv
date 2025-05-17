package com.example.bookadv.services;

import java.util.List;
import java.util.Optional;

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
    public void ChangePassword(Long userId,ChangePasswordDto dto) throws Exception {
        Usuario u = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado."));
    

        //1.- Verificamos que la actual coincide
        if (!passwordEncoder.matches(dto.getCurrentPassword(), u.getPassword())) {
            throw new Exception("La contraseña actual no coincide.");
        }

        // 2.- Verificamos la confirmación.
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("La nueva contraseña y su confirmación no coinciden");
        }

        // 3.- Encriptamos y guardamos
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(u);
    }
}