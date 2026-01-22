package com.ordersystem.infrastructure.mapper;

import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.infrastructure.persistence.entity.OrderEntity;
import com.ordersystem.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper entre Order/OrderItem (dominio) y OrderEntity/OrderItemEntity (JPA)
 */
@Component
public class OrderMapper {
    /**
     * Convierte de OrderEntity a Order (Dominio)
     */
    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return  null;
        }

        Order order = new Order();
        order.setId(entity.getId());
        order.setUserId(entity.getUserId());
        order.setStatus(entity.getStatus());
        order.setTotal(new Money(entity.getTotalAmount(), entity.getTotalCurrency()));
        order.setAddress(entity.getAddress());

        if (entity.getItems() != null) {
            order.setItems(entity.getItems().stream()
                    .map(this::itemToDomain)
                    .collect(Collectors.toList()));
        }
        return order;
    }

    /**
     * Convierte Order (dominio) a OrderEntity (JPA)
     */
    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return  null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.getTotal().getAmount());
        entity.setTotalCurrency(order.getTotal().getCurrency());
        entity.setAddress(order.getAddress());

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemEntity itemEntity = itemToEntity(item);
                entity.addItem(itemEntity);
            }
        }
        return entity;
    }

    /**
     * Convierte OrderItemEntity a OrderItem (dominio)
     */
    public OrderItem itemToDomain(OrderItemEntity entity) {
        if (entity == null) {
            return  null;
        }

        OrderItem item = new OrderItem();
        item.setId(entity.getId());
        item.setProductId(entity.getProductId());
        item.setQuantity(entity.getQuantity());
        item.setUnitPrice(new Money(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency()));
        item.setSubtotal(new Money(entity.getSubtotalAmount(), entity.getSubtotalCurrency()));

        return item;
    }

    /**
     * Convierte de OrderItem a OrderItemEntity
     */
    public OrderItemEntity itemToEntity(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setProductId(item.getProductId());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPriceAmount(item.getUnitPrice().getAmount());
        entity.setUnitPriceCurrency(item.getUnitPrice().getCurrency());
        entity.setSubtotalAmount(item.getSubtotal().getAmount());
        entity.setSubtotalCurrency(item.getSubtotal().getCurrency());

        return entity;
    }
}
