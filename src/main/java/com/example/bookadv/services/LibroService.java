package com.example.bookadv.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookadv.DTO.LibroDTO;
import com.example.bookadv.domain.Libro;
import com.example.bookadv.repository.LibroRepository;

@Service
public class LibroService {
    
    @Autowired
    private LibroRepository libroRepository;

    private final ModelMapper modelMapper;

    public LibroService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    //Añadir nuevo libro
    public void agregarLibro(Libro libro) {
        libro.setFechaAlta(java.time.LocalDate.now());
        libroRepository.save(libro);
    }

    //Obtener todos los libros
    public List<LibroDTO> obtenerTodos() {
        List<Libro> libros = libroRepository.findAll();
        return libros.stream().map(libro -> modelMapper.map(libro, LibroDTO.class)).collect(Collectors.toList());
    }

    //Buscar libro por ID
    public Optional<Libro> obtenerPorId(Long id) { 
        return libroRepository.findById(id);
    }

    //Eliminar un libro por id
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    //Actualizar un libro
    public void actualizarLibro(Libro libro) {
        libroRepository.save(libro);
    }

    public List<LibroDTO> filtrarLibros(String buscar, Long generoId) {
        List<Libro> entidades;

        if ((buscar != null && !buscar.isBlank()) && generoId != null) {
            entidades = libroRepository.findByTituloContainingAndGeneroId(buscar, generoId);
        } else if (buscar != null && !buscar.isBlank()) {
            entidades = libroRepository.findByTituloContaining(buscar);
        } else if (generoId != null) {
            entidades = libroRepository.findByGeneroId(generoId);
        } else {
            entidades = libroRepository.findAll();
        }

        return entidades.stream().map(l -> modelMapper.map(l, LibroDTO.class)).toList();
    }
}
