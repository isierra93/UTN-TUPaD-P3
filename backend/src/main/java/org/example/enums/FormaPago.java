package org.example.enums;

public enum FormaPago {
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    EFECTIVO("Efectivo");

    private final String detalle;

    FormaPago(String detalle) {
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
