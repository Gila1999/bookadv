package com.example.bookadv.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bookadv.DTO.ChangePasswordDto;
import com.example.bookadv.services.UsuarioService;

import jakarta.validation.Valid;



@Controller
@RequestMapping("/usuarios")
public class PerfilController {

  
    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Formulario para cambiar contraseña */
    @GetMapping("/cambiar-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "user/changePasswordView";
    }

    /** Procesamos el cambio de contraseña */
    @PostMapping("/cambiar-password")
    public String proccessChangePassword(
                @Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto,
                BindingResult bindingResult,
                Principal principal,
                Model model) {
        if (bindingResult.hasErrors()) {
            return "user/changePasswordView";
        }

        try {
            //Recuperamos el usuario por nombre
            Long userId = usuarioService.findByNombre(principal.getName())
            .orElseThrow(() -> new Exception("Usuario no encontrado"))
            .getId();

            usuarioService.ChangePassword(userId, dto);
            model.addAttribute("successMessage", "Contraseña cambiada con exito");
            return "user/changePasswordView";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/changePasswordView";
        }
    }

    @GetMapping("/logout-exitoso")
    public String logoutSuccess() {
        return "public/registro/logoutView";
    }
    
    
}   
