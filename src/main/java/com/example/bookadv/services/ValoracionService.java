package com.example.bookadv.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookadv.domain.Libro;
import com.example.bookadv.domain.Valoracion;
import com.example.bookadv.repository.ValoracionRepository;

import jakarta.transaction.Transactional;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

    public List<Valoracion> findAll() {
        return valoracionRepository.findAll();
    }

    public Optional<Valoracion> findById(Long id) {
        return valoracionRepository.findById(id);
    }

    @Transactional
    public Valoracion crearValoracion(Valoracion v) {
        // Verifica que no exista una valoración previa del mismo usuario y libro
        valoracionRepository.findByUsuarioAndLibro(v.getUsuario(), v.getLibro())
            .ifPresent(existing -> { throw new ValoracionDuplicadaException(v); });

        Libro libro = v.getLibro();
        libro.setSumaValoraciones(libro.getSumaValoraciones() + v.getPuntuacion());
        libro.setTotalValoraciones(libro.getTotalValoraciones() + 1);
        libro.setPuntuacionMedia(libro.getSumaValoraciones() * 1.0 / libro.getTotalValoraciones());

        return valoracionRepository.save(v);
    }

    @Transactional
    public Valoracion actualizarValoracion(Valoracion nuevaValoracion) {
        Valoracion antigua = valoracionRepository.findById(nuevaValoracion.getId())
            .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada con ID: " + nuevaValoracion.getId()));

        Libro libro = antigua.getLibro();

        // Ajustar la suma de valoraciones
        libro.setSumaValoraciones(
            libro.getSumaValoraciones() - antigua.getPuntuacion() + nuevaValoracion.getPuntuacion()
        );

        libro.setPuntuacionMedia(libro.getSumaValoraciones() * 1.0 / libro.getTotalValoraciones());

        // Actualizar campos editables
        antigua.setPuntuacion(nuevaValoracion.getPuntuacion());
        antigua.setComentario(nuevaValoracion.getComentario());

        return valoracionRepository.save(antigua);
    }

    public void deleteById(Long id) {
        valoracionRepository.deleteById(id);
    }

    public List<Valoracion> findByLibro(Libro libro) {
        return valoracionRepository.findByLibro(libro);
    }
}
