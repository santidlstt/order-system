package com.ordersystem.domain.exception;

/**
 * Excepción lanzada cuando no hay stock suficiente para procesar un pedido.
 * Se mapea a HTTP 400 Bad Request.
 */
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final String productName;
    private final int requestedQuantity;
    private final int availableStock;

    public InsufficientStockException(Long productId, String productName, int requestedQuantity, int availableStock) {
        super(String.format("Stock insuficiente para el producto '%s'. Solicitado: %d, Disponible: %d",
                productName, requestedQuantity, availableStock));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}