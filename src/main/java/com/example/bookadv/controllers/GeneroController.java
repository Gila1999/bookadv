package com.example.bookadv.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bookadv.domain.Genero;
import com.example.bookadv.services.GeneroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/generos")
public class GeneroController {
    
    @Autowired
    private GeneroService generoService;

    @GetMapping
    public String listarGeneros(Model model) {
        model.addAttribute("generos", generoService.obtenerTodos());
        return "genero/generoListView";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("genero", new Genero());
        return "genero/formNewGeneroView";
    }

    @PostMapping("/nuevo")
    public String guardarGenero(@Valid @ModelAttribute Genero genero, BindingResult result) {
        if (result.hasErrors()) {
            return "genero/formNewGeneroView";
        }
        generoService.guardar(genero);
        return "redirect:/generos";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Genero> genero = generoService.obtenerPorId(id);
        if (genero.isPresent()) {
            model.addAttribute("genero", genero.get());
            return "genero/formEditGeneroView";
        }
        return "redirect:/generos";
    }
    
    @PostMapping("/editar")
    public String editarGenero(@ModelAttribute Genero genero) {
        generoService.guardar(genero);        
        return "redirect:/generos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGenero(@PathVariable Long id) {
        generoService.eliminar(id);
        return "redirect:/generos";
    }    
}
