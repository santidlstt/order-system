package com.ordersystem.application.order;

import com.ordersystem.api.order.dto.CreateOrderRequest;
import com.ordersystem.api.order.dto.OrderItemRequest;
import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.model.valueobject.Address;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para crear pedidos
 * Valida stock pero NO lo descuenta (se descuenta al pagar)
 */
@Service
@RequiredArgsConstructor
public class CreateOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order execute(Long userId, CreateOrderRequest request) {
        // 1. Crear el pedido
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(new Address(request.getStreet(), request.getCity(), request.getCountry()));

        // 2. Procesar cada item del pedido
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Buscar el producto
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductId()));

            // Validar que esté activo
            if (!product.getActive()) {
                throw new RuntimeException("El producto no está disponible: " + product.getName());
            }

            // Validar stock disponible (sin descontar todavía)
            if (!product.hasStock(itemRequest.getQuantity())) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            // Crear el item del pedido
            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    itemRequest.getQuantity(),
                    product.getPrice()
            );

            order.addItem(orderItem);
        }

        // 3. Calcular el total
        Money total = order.calculateTotal();
        order.setTotal(total);

        // 4. Guardar el pedido
        return orderRepository.save(order);
    }
}