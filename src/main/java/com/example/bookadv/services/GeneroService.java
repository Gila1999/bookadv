package com.example.bookadv.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookadv.domain.Genero;
import com.example.bookadv.repository.GeneroRepository;

@Service
public class GeneroService {
    
    @Autowired
    private GeneroRepository generoRepository;

    public List<Genero> obtenerTodos() {
        return generoRepository.findAll();
    }

    public Optional<Genero> obtenerPorId(Long id) {
        return generoRepository.findById(id);
    }

    public void guardar(Genero genero) {
        generoRepository.save(genero);
    }

    public void eliminar(Long id) {
        generoRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return generoRepository.existsByNombre(nombre);
    }
}
