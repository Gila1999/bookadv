package com.example.bookadv.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.bookadv.domain.Valoracion;
import com.example.bookadv.services.LibroService;
import com.example.bookadv.services.UsuarioService;
import com.example.bookadv.services.ValoracionDuplicadaException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final UsuarioService usuarioService;

    private final LibroService libroService;

    GlobalExceptionHandler(LibroService libroService, UsuarioService usuarioService) {
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }
    
    @ExceptionHandler(ValoracionDuplicadaException.class)
    public String handleDuplicada(ValoracionDuplicadaException ex, Model model) {
        Valoracion valoracion = ex.getValoracion();
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("valoracion", valoracion); 
        model.addAttribute("libro", valoracion.getLibro());
        model.addAttribute("usuarios", usuarioService.findAll());

        return "review/formNewValoracionView"; 
    }
}
