package com.example.bookadv.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookadv.domain.Libro;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.domain.Valoracion;
import com.example.bookadv.services.LibroService;
import com.example.bookadv.services.UsuarioService;
import com.example.bookadv.services.ValoracionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/valoraciones")
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    // ---------------------------------------------------
    // 1) Listar todas las valoraciones (ADMIN/MANAGER)
    // ---------------------------------------------------
    @GetMapping
    public String listarValoraciones(Model model) {
        model.addAttribute("valoraciones", valoracionService.findAll());
        return "review/valoracionListView";
    }

    // ---------------------------------------------------
    // 2) Listar valoraciones de un libro (público)
    // ---------------------------------------------------
    @GetMapping("/libro/{id}")
    public String listarPorLibro(@PathVariable Long id, Model model) {
        Libro libro = libroService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        List<Valoracion> list = valoracionService.findByLibro(libro);
        model.addAttribute("libro", libro);
        model.addAttribute("valoraciones", list);
        return "review/valoracionListView";
    }

    // ---------------------------------------------------
    // 3) Mostrar formulario de nueva valoración
    // ---------------------------------------------------
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(
            @RequestParam(value = "libroId", required = false) Long libroId,
            Model model) {

        Valoracion dto = new Valoracion();
        if (libroId != null) {
            Libro libro = libroService.obtenerPorId(libroId)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            dto.setLibro(libro);
        }
        model.addAttribute("valoracion", dto);
        model.addAttribute("libros", libroService.obtenerTodos());
        return "review/formNewValoracionView";
    }

    // ---------------------------------------------------
    // 4) Procesar formulario de nueva valoración
    // ---------------------------------------------------
    @PostMapping("/save")
    public String guardarValoracion(
            @Valid @ModelAttribute("valoracion") Valoracion valoracion,
            BindingResult br,
            Principal principal,
            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("libros", libroService.obtenerTodos());
            return "review/formNewValoracionView";
        }

        // 1) Asociar usuario
        Usuario user = usuarioService
            .findByNombre(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("El usuario no ha sido encontrado"));
        valoracion.setUsuario(user);

        // 2) Guardar la valoración
        Valoracion guardada = valoracionService.crearValoracion(valoracion);

        // 3) Recuperar el libro *completo* de la BD
        Long libroId = guardada.getLibro().getId();
        Libro libro = libroService.obtenerPorId(libroId)
                .orElseThrow(() -> new IllegalStateException("Libro no encontrado"));

        // 4) Recalcular medias con null-check
        int prevTotal = Optional.ofNullable(libro.getTotalValoraciones()).orElse(0);
        double prevSum = Optional.ofNullable(libro.getSumaValoraciones()).orElse(0.0);
        libro.setTotalValoraciones(prevTotal + 1);
        libro.setSumaValoraciones(prevSum + guardada.getPuntuacion());
        libro.setPuntuacionMedia((prevSum + guardada.getPuntuacion()) / (prevTotal + 1));

        // 5) Salvar el libro ya íntegro
        libroService.actualizarLibro(libro);

        return "redirect:/valoraciones/libro/" + libroId;
    }


    // ---------------------------------------------------
    // 5) Mostrar formulario de edición (ADMIN/MANAGER)
    // ---------------------------------------------------
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Valoracion v = valoracionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));
        model.addAttribute("valoracion", v);
        model.addAttribute("libros", libroService.obtenerTodos());
        return "review/formEditValoracionView";
    }

    // ---------------------------------------------------
    // 6) Procesar edición de valoración
    // ---------------------------------------------------
    @PostMapping("/actualizar")
    public String actualizarValoracion(
            @Valid @ModelAttribute("valoracion") Valoracion form,
            BindingResult br,
            Principal principal,
            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("libros", libroService.obtenerTodos());
            return "review/formEditValoracionView";
        }

        Valoracion original = valoracionService.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));

        // Opcional: comprobar que principal es creador o ADMIN
        Usuario user = usuarioService.findByNombre(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        boolean esAdmin = user.getRol().name().equals("ADMIN");
        if (!esAdmin && !original.getUsuario().equals(user)) {
            return "redirect:/accessError";
        }

        original.setComentario(form.getComentario());
        original.setPuntuacion(form.getPuntuacion());
        // no tocamos usuario ni libro

        valoracionService.actualizarValoracion(original);
        return "redirect:/valoraciones/libro/" + original.getLibro().getId();
    }

    // ---------------------------------------------------
    // 7) Eliminar valoración (creador o ADMIN)
    // ---------------------------------------------------
    @GetMapping("/eliminar/{id}")
    public String eliminarValoracion(@PathVariable Long id, Principal principal) {
        Valoracion v = valoracionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));

        Usuario user = usuarioService.findByNombre(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        boolean esAdmin = user.getRol().name().equals("ADMIN");

        if (esAdmin || v.getUsuario().equals(user)) {
            valoracionService.deleteById(id);
            return "redirect:/valoraciones/libro/" + v.getLibro().getId();
        } else {
            return "redirect:/accessError";
        }
    }
}
