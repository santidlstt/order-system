package com.ordersystem.domain.exception;

/**
 * Excepción lanzada cuando se intenta operar con un producto inactivo.
 * Se mapea a HTTP 400 Bad Request.
 */
public class InactiveProductException extends RuntimeException {

    private final Long productId;
    private final String productName;

    public InactiveProductException(Long productId, String productName) {
        super(String.format("El producto '%s' no está disponible", productName));
        this.productId = productId;
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}