package com.example.bookadv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookadv.domain.Libro;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.domain.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    Optional<Valoracion> findByUsuarioAndLibro(Usuario usuario, Libro libro);
    List<Valoracion> findByLibro(Libro libro);

}
