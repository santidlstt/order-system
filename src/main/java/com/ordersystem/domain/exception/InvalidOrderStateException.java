package com.ordersystem.domain.exception;

import com.ordersystem.domain.enums.OrderStatus;

/**
 * Excepción lanzada cuando se intenta realizar una operación inválida dado el estado actual del pedido.
 * Se mapea a HTTP 409 Conflict.
 */
public class InvalidOrderStateException extends RuntimeException {

    private final Long orderId;
    private final OrderStatus currentStatus;
    private final String operation;

    public InvalidOrderStateException(Long orderId, OrderStatus currentStatus, String operation) {
        super(String.format("No se puede %s el pedido %d en estado %s", operation, orderId, currentStatus));
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.operation = operation;
    }

    public InvalidOrderStateException(String message) {
        super(message);
        this.orderId = null;
        this.currentStatus = null;
        this.operation = null;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getOperation() {
        return operation;
    }
}