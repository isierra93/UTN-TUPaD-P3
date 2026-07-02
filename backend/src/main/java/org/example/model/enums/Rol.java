package org.example.model.enums;

public enum Rol {
    ADMIN("Administrador"),
    USUARIO("Usuario");

    private final String detalle;

    Rol(String detalle) {
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
