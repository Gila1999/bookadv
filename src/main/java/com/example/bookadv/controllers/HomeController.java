package com.example.bookadv.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class HomeController {

    @GetMapping({"", "/"})
    public String home() {
        return "public/home/indexView";
    }

    @GetMapping("/quienes-somos")
    public String quienesSomos() {
        return "public/home/quienes-somosView";
    }
}
