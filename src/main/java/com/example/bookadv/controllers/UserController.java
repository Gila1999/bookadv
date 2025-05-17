package com.example.bookadv.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bookadv.DTO.ChangePasswordDto;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.services.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    // -------------------- EDITAR PERFIL --------------------

    /**
     * Mostrar formulario de edición de perfil.
     */
    @GetMapping("/editar-perfil")
    public String showEditProfile(Model model, Principal principal) {
        // Obtener Usuario o lanzar si no existe
        Usuario usuario = usuarioService.findByNombre(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(
            "No se ha encontrado ningun usuario con nombre: " + principal.getName()));
        model.addAttribute("usuario", usuario);
        return "user/edit-profile";
    }

    /**
     * Procesar envío del formulario de edición de perfil.
     */
    @PostMapping("/editar-perfil")
    public String processEditProfile(
            @Valid @ModelAttribute("usuario") Usuario usuarioForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "user/edit-profile";
        }
        // Actualizar sólo los campos permitidos (p. ej. nombre)
        usuarioService.updateProfile(principal.getName(), usuarioForm);
        return "redirect:/public/inicio";
    }

    // -------------------- CAMBIAR CONTRASEÑA --------------------

    /**
     * Mostrar formulario de cambio de contraseña.
     */
    @GetMapping("/cambiar-password")
    public String showChangePassword(Model model) {
        model.addAttribute("passwordDto", new ChangePasswordDto());
        return "user/change-password";
    }

    /**
     * Procesar cambio de contraseña. Valida actual y que nueva==confirm.
     */
    @PostMapping("/cambiar-password")
    public String processChangePassword(
            @Valid @ModelAttribute("passwordDto") ChangePasswordDto dto,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "user/change-password";
        }
        try {
            usuarioService.changePassword(principal.getName(), dto);
            model.addAttribute("successMessage", "Contraseña cambiada con éxito");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "user/change-password";
    }

    // -------------------- LOGOUT EXITOSO --------------------

    /**
     * Página tras logout exitoso.
     */
    @GetMapping("/logout-exitoso")
    public String logoutSuccess() {
        return "public/logout-success";
    }
}
