package com.example.bookadv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookadv.domain.Role;
import com.example.bookadv.domain.Usuario;
import com.example.bookadv.services.UsuarioService;

import jakarta.validation.Valid;


@Controller
public class AuthController {

    @Autowired
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Pagina principal de login
    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            @RequestParam(name = "signupSuccess", required = false) String signupSuccess,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Creedenciales invalidas");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "Has cerrado sesión correctamente.");
        }
        if (signupSuccess != null) {
            model.addAttribute("infoMessage", "Registro completado. Ya puedes iniciar sesión.");
        }
        return "public/registro/loginView"; 
    }
    
    // Mostrar formulario de registro
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "public/registro/signupView";
    }

    @PostMapping("/signup/save")
    public String proccessSignup(
                @Valid @ModelAttribute("usuario") Usuario usuario,
                BindingResult bindingResult,
                Model model) {
        if (bindingResult.hasErrors()) {
            return "public/registro/signupView";
        } 

        try {
            usuario.setRol(Role.USER);
            usuarioService.save(usuario);
            return "redirect:/login?signupSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error al registrar el usuario: " + e.getMessage());
            return "public/registro/signupView";
        }
    }

    //** Página de acceso denegado */
    @GetMapping("/accessError")
    public String accessDenied() {
        return "error/";
    }
    
    
}
