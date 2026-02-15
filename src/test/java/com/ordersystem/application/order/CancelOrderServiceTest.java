package com.ordersystem.application.order;

import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
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
 * Tests unitarios para CancelOrderService
 * Verifica la devolución de stock según el estado del pedido
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderService - Tests Unitarios")
class CancelOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CancelOrderService cancelOrderService;

    private Order order;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        // Preparar producto
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(Money.usd(new BigDecimal("100.00")));
        product.setStock(8); // Ya se descontaron 2 unidades
        product.setActive(true);

        // Preparar item del pedido
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(Money.usd(new BigDecimal("100.00")));
        orderItem.setSubtotal(Money.usd(new BigDecimal("200.00")));

        // Preparar pedido
        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(Money.usd(new BigDecimal("200.00")));
        order.setItems(Collections.singletonList(orderItem));
    }

    @Test
    @DisplayName("Debe cancelar pedido CREATED sin devolver stock")
    void shouldCancelCreatedOrderWithoutReturningStock() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = cancelOrderService.execute(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        // Verificar que NO se devolvió stock (porque estaba CREATED)
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cancelar pedido PAID y devolver stock")
    void shouldCancelPaidOrderAndReturnStock() {
        // Given
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = cancelOrderService.execute(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStock()).isEqualTo(10); // 8 + 2 = 10 (stock devuelto)

        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Debe fallar cuando el pedido no existe")
    void shouldFailWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cancelOrderService.execute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pedido no encontrado");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el pedido está SHIPPED")
    void shouldFailWhenOrderIsShipped() {
        // Given
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThatThrownBy(() -> cancelOrderService.execute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no puede ser cancelado");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el pedido ya está CANCELLED")
    void shouldFailWhenOrderIsAlreadyCancelled() {
        // Given
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThatThrownBy(() -> cancelOrderService.execute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no puede ser cancelado");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el producto no existe al devolver stock")
    void shouldFailWhenProductNotFoundWhileReturningStock() {
        // Given
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cancelOrderService.execute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");

        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe devolver stock correctamente con múltiples items")
    void shouldReturnStockCorrectlyWithMultipleItems() {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setStock(5);
        product2.setActive(true);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProductId(2L);
        orderItem2.setQuantity(3);

        order.setStatus(OrderStatus.PAID);
        order.setItems(java.util.Arrays.asList(orderItem, orderItem2));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = cancelOrderService.execute(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStock()).isEqualTo(10);  // 8 + 2 = 10
        assertThat(product2.getStock()).isEqualTo(8);  // 5 + 3 = 8

        verify(productRepository, times(2)).save(any(Product.class));
    }
}