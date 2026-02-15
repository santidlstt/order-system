package com.ordersystem.domain.exception;

/**
 * Excepción lanzada cuando un pedido no existe en el sistema.
 * Se mapea a HTTP 404 Not Found.
 */
public class OrderNotFoundException extends RuntimeException {

    private final Long orderId;

    public OrderNotFoundException(Long orderId) {
        super("Pedido no encontrado con ID: " + orderId);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}