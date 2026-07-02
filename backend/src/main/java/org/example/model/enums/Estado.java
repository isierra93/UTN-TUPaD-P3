package org.example.model.enums;

public enum Estado {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    TERMINADO("Terminado"),
    CANCELADO("Cancelado");

    private final String detalle;

    Estado(String detalle) {
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
