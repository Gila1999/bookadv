package com.example.bookadv.services;

import com.example.bookadv.domain.Valoracion;

public class ValoracionDuplicadaException extends RuntimeException {
    private final Valoracion valoracion;

    public ValoracionDuplicadaException(Valoracion valoracion) {
        super("Ya has valorado este libro anteriormente.");
        this.valoracion = valoracion;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }
}

