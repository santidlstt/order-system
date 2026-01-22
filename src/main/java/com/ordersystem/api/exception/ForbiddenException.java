package com.ordersystem.api.exception;

/**
 * Excepción de acceso denegado (403)
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
