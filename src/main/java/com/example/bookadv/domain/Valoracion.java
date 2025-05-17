package com.example.bookadv.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        name = "UK_VALORACION_USUARIO_LIBRO",
        columnNames = {"usuario_id", "libro_id"}
    )
)
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double puntuacion;
    private String comentario;

    //Relacion muchos a uno con Libro
    @ManyToOne
    private Libro libro;

    //Relacion muchos a uno con Usuario
    @ManyToOne
    private Usuario usuario;

}
