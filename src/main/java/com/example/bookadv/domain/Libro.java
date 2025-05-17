package com.example.bookadv.domain;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "libros")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String titulo;

    @Min(1800)
    private int anio;

    @ManyToOne
    @JoinColumn(name = "genero_id")
    private Genero genero;

    //Relacion uno a muchos con valoracion
    @OneToMany(mappedBy = "libro")
    private Set<Valoracion> valoraciones;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    private String autor;
    private String sinopsis;
    private String portada;

    private LocalDate fechaAlta;

    //Para este sprint: puntuación media y sumatorio
    private Double puntuacionMedia = 0.0;
    private Double sumaValoraciones = 0.0;
    private Integer totalValoraciones = 0;
}
