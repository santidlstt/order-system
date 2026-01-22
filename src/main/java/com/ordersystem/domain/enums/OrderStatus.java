package com.ordersystem.domain.enums;

/**
 *  Estados posibles de un pedido
 */
public enum OrderStatus {
    CREATED, // Pedido creado, pendiente de pago
    PAID, // Pedido pagado, pendiente de envío
    SHIPPED, // Pedido enviado
    CANCELLED // Pedido cancelado
}
