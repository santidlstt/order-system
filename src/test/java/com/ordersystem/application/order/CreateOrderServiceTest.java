package com.ordersystem.application.order;

import com.ordersystem.api.order.dto.CreateOrderRequest;
import com.ordersystem.api.order.dto.OrderItemRequest;
import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreateOrderService
 * Usa Mockito para aislar el servicio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderService - Tests Unitarios")
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateOrderService createOrderService;

    private Product product;
    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        // Preparar producto de prueba
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(Money.usd(new BigDecimal("100.00")));
        product.setStock(10);
        product.setActive(true);

        // Preparar request de prueba
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        request = new CreateOrderRequest();
        request.setItems(Collections.singletonList(itemRequest));
        request.setStreet("Calle Test 123");
        request.setCity("Test City");
        request.setCountry("Test Country");
    }

    @Test
    @DisplayName("Debe crear pedido exitosamente con stock suficiente")
    void shouldCreateOrderSuccessfully() {
        // Given
        Long userId = 1L;
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = createOrderService.execute(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotal().getAmount()).isEqualTo(new BigDecimal("200.00"));

        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe fallar cuando el producto no existe")
    void shouldFailWhenProductNotFound() {
        // Given
        Long userId = 1L;
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> createOrderService.execute(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");

        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe fallar cuando el producto está inactivo")
    void shouldFailWhenProductIsInactive() {
        // Given
        Long userId = 1L;
        product.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> createOrderService.execute(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está disponible");

        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe fallar cuando no hay stock suficiente")
    void shouldFailWhenInsufficientStock() {
        // Given
        Long userId = 1L;
        product.setStock(1); // Solo hay 1, pero se piden 2
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> createOrderService.execute(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe calcular el total correctamente con múltiples items")
    void shouldCalculateTotalCorrectlyWithMultipleItems() {
        // Given
        Long userId = 1L;

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setPrice(Money.usd(new BigDecimal("50.00")));
        product2.setStock(5);
        product2.setActive(true);

        OrderItemRequest item1 = new OrderItemRequest(1L, 2); // 2 x $100 = $200
        OrderItemRequest item2 = new OrderItemRequest(2L, 3); // 3 x $50 = $150
        request.setItems(java.util.Arrays.asList(item1, item2));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = createOrderService.execute(userId, request);

        // Then
        assertThat(result.getTotal().getAmount()).isEqualTo(new BigDecimal("350.00"));
        assertThat(result.getItems()).hasSize(2);
    }
}