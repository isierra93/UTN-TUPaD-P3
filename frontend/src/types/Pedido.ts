import type { DetallePedido } from "./DetallePedido";

export type EstadoPedido = "PENDIENTE" | "CONFIRMADO" | "TERMINADO" | "CANCELADO";
export type FormaPago = "TARJETA" | "TRANSFERENCIA" | "EFECTIVO";

export interface Pedido {
    id: number;
    fecha: string;
    estado: EstadoPedido;
    total: number;
    formaPago: FormaPago;
    idUsuario: number;
    detalles: DetallePedido[];
}
