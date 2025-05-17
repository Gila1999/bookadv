package com.example.bookadv.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.example.bookadv.domain.Role;

@Controller
@RequestMapping("/valoraciones")
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar todas las valoraciones (solo ADMIN o MANAGER)
    @GetMapping
    public String listarValoraciones(Model model) {
        model.addAttribute("valoraciones", valoracionService.findAll());
        return "review/valoracionListView";
    }

    // Listar valoraciones de un libro (público)
    @GetMapping("/libro/{id}")
    public String listarValoracionesPorLibro(@PathVariable Long id, Model model) {
        Libro libro = libroService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        List<Valoracion> valoraciones = valoracionService.findByLibro(libro);
        model.addAttribute("libro", libro);
        model.addAttribute("valoraciones", valoraciones);
        return "review/valoracionListView";
    }

    // Mostrar formulario para nueva valoración de un libro (usuario logueado)
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(@RequestParam(required = false) Long libroId, Model model) {
        Valoracion valoracion = new Valoracion();
        if (libroId != null) {
            Libro libro = libroService.obtenerPorId(libroId)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            valoracion.setLibro(libro);
        }
        model.addAttribute("valoracion", valoracion);
        model.addAttribute("libros", libroService.obtenerTodos());
        return "review/formNewValoracionView";
    }

    // Guardar valoración asociada al usuario logueado
    @PostMapping("/guardar")
    public String guardarValoracion(@ModelAttribute Valoracion valoracion) {
        // Obtener usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username;
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        Usuario usuario = usuarioService.findByNombre(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + username));

        valoracion.setUsuario(usuario);

        // Guardar valoración
        Valoracion vGuardada = valoracionService.crearValoracion(valoracion);

        // Actualizar datos del libro (puntuación media)
        Libro libro = libroService.obtenerPorId(vGuardada.getLibro().getId())
                .orElseThrow(() -> new IllegalStateException("Libro no encontrado"));

        int total = libro.getTotalValoraciones() + 1;
        Double suma = libro.getSumaValoraciones() + vGuardada.getPuntuacion();
        libro.setTotalValoraciones(total);
        libro.setSumaValoraciones(suma);
        libro.setPuntuacionMedia(suma / total);

        libroService.actualizarLibro(libro);

        return "redirect:/valoraciones/libro/" + libro.getId();
    }

    // Mostrar formulario de edición solo para ADMIN o MANAGER (seguridad en backend)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Valoracion valoracion = valoracionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));
        model.addAttribute("valoracion", valoracion);
        model.addAttribute("libros", libroService.obtenerTodos());
        return "review/formEditValoracionView";
    }

    // Actualizar valoración (sin cambiar usuario)
    @PostMapping("/actualizar")
    public String actualizarValoracion(@ModelAttribute Valoracion valoracion) {
        Valoracion original = valoracionService.findById(valoracion.getId())
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));

        original.setComentario(valoracion.getComentario());
        original.setPuntuacion(valoracion.getPuntuacion());
        // No cambiamos usuario ni libro

        Valoracion actualizada = valoracionService.actualizarValoracion(original);

        return "redirect:/valoraciones/libro/" + actualizada.getLibro().getId();
    }

    // Eliminar valoración solo si eres creador o ADMIN
    @GetMapping("/eliminar/{id}")
    public String eliminarValoracion(@PathVariable Long id) {
        Valoracion valoracion = valoracionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Valoración no encontrada"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username;
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        Usuario usuario = usuarioService.findByNombre(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + username));

        // Solo puede eliminar si es ADMIN o creador
        if (usuario.getRol() == Role.ADMIN || usuario.equals(valoracion.getUsuario())) {
            valoracionService.deleteById(id);
            return "redirect:/valoraciones/libro/" + valoracion.getLibro().getId();
        } else {
            // Acceso denegado
            return "redirect:/accessError";
        }
    }
}
