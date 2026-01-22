package com.ordersystem.domain.model;

import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.valueobject.Address;
import com.ordersystem.domain.model.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dominio para Pedido
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private Money total;
    private Address address;
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Agregar item al pedido
     */
    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Calcular el total del pedido
     */
    public Money calculateTotal() {
        Money sum = Money.usd(java.math.BigDecimal.ZERO);
        for (OrderItem item : items) {
            sum = sum.add(item.getSubtotal());
        }
        return sum;
    }

    /**
     * Verificar si el pedido puede ser cancelado
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.CREATED || status == OrderStatus.PAID;
    }

    /**
     * Verificar si el pedido puede ser pagado
     */
    public boolean canBePaid() {
        return status == OrderStatus.CREATED;
    }

    /**
     * Verificar si el pedido puede ser enviado
     */
    public boolean canBeShipped() {
        return status == OrderStatus.PAID;
    }
}