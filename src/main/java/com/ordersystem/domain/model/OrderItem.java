package com.ordersystem.domain.model;

import com.ordersystem.domain.model.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de dominio para Item de Pedido
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Money unitPrice;
    private Money subtotal;

    /**
     * Constructor para crear un item con cálculo automático de subtotal
     */
    public OrderItem(Long productId, Integer quantity, Money unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(quantity);
    }

    /**
     * Recalcular subtotal
     */
    public void calculateSubtotal() {
        this.subtotal = this.unitPrice.multiply(this.quantity);
    }
}