package com.ordersystem.application.order;

import com.ordersystem.api.order.dto.AddressResponse;
import com.ordersystem.api.order.dto.OrderItemResponse;
import com.ordersystem.api.order.dto.OrderResponse;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para consultar pedidos
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setTotal(order.getTotal().getAmount());
        response.setCurrency(order.getTotal().getCurrency());

        // Mapear dirección
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setStreet(order.getAddress().getStreet());
        addressResponse.setCity(order.getAddress().getCity());
        addressResponse.setCountry(order.getAddress().getCountry());
        response.setAddress(addressResponse);

        // Mapear items con nombre del producto
        List<OrderItemResponse> itemsResponse = order.getItems().stream()
                .map(item -> mapItemToResponse(item))
                .collect(Collectors.toList());
        response.setItems(itemsResponse);

        return response;
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice().getAmount());
        response.setSubtotal(item.getSubtotal().getAmount());
        response.setCurrency(item.getUnitPrice().getCurrency());

        // Obtener nombre del producto
        productRepository.findById(item.getProductId())
                .ifPresent(product -> response.setProductName(product.getName()));

        return response;
    }
}