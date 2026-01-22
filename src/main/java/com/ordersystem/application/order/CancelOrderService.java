package com.ordersystem.application.order;

import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para cancelar pedidos
 * Si el pedido estaba PAID, devuelve el stock
 */
@Service
@RequiredArgsConstructor
public class CancelOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order execute(Long orderId) {
        // 1. Buscar el pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

        // 2. Validar que se pueda cancelar
        if (!order.canBeCancelled()) {
            throw new RuntimeException("El pedido no puede ser cancelado. Estado actual: " + order.getStatus());
        }

        // 3. Si el pedido estaba PAID, devolver el stock
        if (order.getStatus() == OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductId()));

                // Devolver stock
                product.increaseStock(item.getQuantity());
                productRepository.save(product);
            }
        }

        // 4. Cambiar estado a CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}