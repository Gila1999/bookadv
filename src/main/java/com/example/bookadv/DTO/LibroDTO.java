package com.example.bookadv.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String sinopsis;
    private String idioma;
    private String portada;

}
