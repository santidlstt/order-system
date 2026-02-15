package com.ordersystem.domain.exception;

/**
 * Excepción lanzada cuando un producto no existe en el sistema.
 * Se mapea a HTTP 404 Not Found.
 */
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Producto no encontrado con ID: " + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}