package com.example.bookadv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookadv.domain.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    boolean existsByNombre(String nombre);
}
