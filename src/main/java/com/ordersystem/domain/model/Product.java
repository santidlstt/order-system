package com.ordersystem.domain.model;

import com.ordersystem.domain.model.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de dominio para Producto
 * POJO puro sin dependencias de frameworks
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private Money price;
    private Integer stock;
    private Boolean active;

    /**
     * Verifica si hay stock suficiente
     */
    public boolean hasStock(int quantity) {
        return this.active && this.stock >= quantity;
    }

    /**
     * Reduce el stock (Para cuando se realiza un pedido)
     */
    public void reduceStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + this.name);
        }
        this.stock -= quantity;
    }

    /**
     * Incremente el stock (Para cuando se cancela un pedido)
     */
    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}
