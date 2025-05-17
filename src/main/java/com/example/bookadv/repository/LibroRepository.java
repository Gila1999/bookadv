package com.example.bookadv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookadv.domain.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByTituloContaining(String titulo);
    List<Libro> findByGeneroId(Long generoId);
    List<Libro> findByTituloContainingAndGeneroId(String titulo, Long generoId);
    
}