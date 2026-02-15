package com.ordersystem.application.order;

import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.enums.PaymentStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.Payment;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.PaymentRepository;
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
 * Tests unitarios para PayOrderService
 * Incluye tests de idempotencia y descuento de stock
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PayOrderService - Tests Unitarios")
class PayOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PayOrderService payOrderService;

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
        product.setStock(10);
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
    @DisplayName("Debe procesar pago exitosamente y descontar stock")
    void shouldPayOrderSuccessfullyAndReduceStock() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        // When
        Payment result = payOrderService.execute(1L, "credit_card");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(result.getPaymentMethod()).isEqualTo("credit_card");
        assertThat(result.getTransactionId()).isNotNull();
        assertThat(result.getTransactionId()).startsWith("TXN-");

        // Verificar que se descontó el stock
        assertThat(product.getStock()).isEqualTo(8); // 10 - 2 = 8

        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        verify(orderRepository, times(1)).save(order);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe ser idempotente - no procesar pago duplicado")
    void shouldBeIdempotent() {
        // Given
        Payment existingPayment = new Payment();
        existingPayment.setId(1L);
        existingPayment.setOrderId(1L);
        existingPayment.setStatus(PaymentStatus.APPROVED);

        when(paymentRepository.existsByOrderId(1L)).thenReturn(true);
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(existingPayment));

        // When
        Payment result = payOrderService.execute(1L, "credit_card");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED);

        // Verificar que NO se procesó el pago nuevamente
        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(paymentRepository, times(1)).findByOrderId(1L);
        verify(orderRepository, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el pedido no existe")
    void shouldFailWhenOrderNotFound() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> payOrderService.execute(1L, "credit_card"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pedido no encontrado");

        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, never()).findById(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el pedido no está en estado CREATED")
    void shouldFailWhenOrderNotInCreatedStatus() {
        // Given
        order.setStatus(OrderStatus.PAID); // Ya está pagado
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThatThrownBy(() -> payOrderService.execute(1L, "credit_card"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no puede ser pagado");

        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, never()).findById(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar cuando no hay stock suficiente")
    void shouldFailWhenInsufficientStock() {
        // Given
        product.setStock(1); // Solo hay 1, pero se necesitan 2
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> payOrderService.execute(1L, "credit_card"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar cuando el producto no existe")
    void shouldFailWhenProductNotFound() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> payOrderService.execute(1L, "credit_card"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");

        verify(paymentRepository, times(1)).existsByOrderId(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cambiar el estado del pedido a PAID")
    void shouldChangeOrderStatusToPaid() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        // When
        payOrderService.execute(1L, "credit_card");

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).save(order);
    }
}