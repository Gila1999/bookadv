package com.example.bookadv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.bookadv.DTO.FormInfo;
import com.example.bookadv.services.EmailService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;





@Controller
@RequestMapping("public")
public class ContactController {        
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/contacta")
    public String mostrarFormulario(Model model) {

        FormInfo formInfo = new FormInfo();

        model.addAttribute("formInfo", formInfo);


        return "public/contact/contactaView";
    }

    @PostMapping("/enviarContacto")
    public String enviarFormularioContacto(@ModelAttribute FormInfo formInfo, Model model){
        try {
            emailService.enviarCorreoContacto(formInfo.getNombre(), formInfo.getEmail(), formInfo.getMotivo());
            return "public/contact/contactoExitosoView";
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un problema al enviar el mensaje.");
            return "public/contact/contactaView";  
        }
    }

}
    

