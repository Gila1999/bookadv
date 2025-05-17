package com.example.bookadv.DTO;

public class FormInfo {
    private String nombre; 
    private String email; 
    private String motivo; 
    private boolean aceptar;

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public void setAceptar(boolean aceptar) {
        this.aceptar = aceptar;
    }
    public String getNombre() {
        return nombre;
    }
    public String getEmail() {
        return email;
    }
    public String getMotivo() {
        return motivo;
    }
    public boolean isAceptar() {
        return aceptar;
    } 


}
