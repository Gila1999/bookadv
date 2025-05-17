package com.example.bookadv.controllers;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookadv.DTO.LibroDTO;
import com.example.bookadv.domain.Idioma;
import com.example.bookadv.domain.Libro;
import com.example.bookadv.services.GeneroService;
import com.example.bookadv.services.LibroService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/libros")
public class LibroController {


    @Autowired
    private LibroService libroService;

    @Autowired
    private GeneroService generoService;

    // Listar libros
    @GetMapping
    public String listarLibros(
        @RequestParam(name = "buscar",  required = false) String buscar,
        @RequestParam(name = "genero",  required = false) Long   generoId,
        Model model) {

        // 1) Traer la lista de géneros para el dropdown
        model.addAttribute("generos", generoService.obtenerTodos());
        model.addAttribute("filtroGeneroId", generoId);

        // 2) Traer libros (como DTOs) aplicando filtro
        List<LibroDTO> libros = libroService.filtrarLibros(buscar, generoId);
        model.addAttribute("libros", libros);

        return "book/bookListView";
    }
    
    // Ver detalle del libro
    @GetMapping("/{id}")
    public String verLibro(@PathVariable Long id, Model model) {
        Optional<Libro> libroOpt = libroService.obtenerPorId(id);
        if (!libroOpt.isPresent()) {
            return "redirect:/libros";
        }
        model.addAttribute("libro", libroOpt.get());
        return "book/bookView";
    }

    // Mostrar formulario para crear un nuevo libro
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoLibro(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("generos", generoService.obtenerTodos());
        model.addAttribute("idiomas", Idioma.values());
        return "book/formNewBookView";
    }

    // Procesar formulario de nuevo libro
    @PostMapping("/nuevo")
    public String agregarLibro(@Valid @ModelAttribute Libro libro, BindingResult result) {
        if (result.hasErrors()) {
            return "book/formNewBookView";
        }
        libroService.agregarLibro(libro);
        return "redirect:/libros";
    }

    // Mostrar formulario para editar un libro existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarLibro(@PathVariable Long id, Model model) {
        Optional<Libro> libroOpt = libroService.obtenerPorId(id);
        if (!libroOpt.isPresent()) {
            return "redirect:/libros";
        }
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("generos", generoService.obtenerTodos());
        model.addAttribute("idiomas", Idioma.values());
        return "book/formEditBookView"; // Asegúrate de tener esta vista
    }

    // Procesar formulario de edición
    @PostMapping("/editar")
    public String editarLibro(@ModelAttribute Libro libro) {
        libroService.actualizarLibro(libro);  // Implementa este método en tu servicio
        return "redirect:/libros";
    }

    // Eliminar libro
    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return "redirect:/libros";
    }
}
