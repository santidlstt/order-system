package com.ordersystem.application.order;

import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.enums.PaymentStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.model.OrderItem;
import com.ordersystem.domain.model.Payment;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.repository.OrderRepository;
import com.ordersystem.domain.repository.PaymentRepository;
import com.ordersystem.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio para pagar pedidos
 * Descuenta stock y registra el pago (IDEMPOTENTE)
 */
@Service
@RequiredArgsConstructor
public class PayOrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Payment execute(Long orderId, String paymentMethod) {
        // 1. Verificar si ya existe un pago (IDEMPOTENCIA)
        if (paymentRepository.existsByOrderId(orderId)) {
            return paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Error al obtener pago existente"));
        }

        // 2. Buscar el pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

        // 3. Validar que se pueda pagar
        if (!order.canBePaid()) {
            throw new RuntimeException("El pedido no puede ser pagado. Estado actual: " + order.getStatus());
        }

        // 4. Descontar stock de cada producto
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductId()));

            // Validar stock nuevamente (por si cambió desde la creación)
            if (!product.hasStock(item.getQuantity())) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar stock
            product.reduceStock(item.getQuantity());
            productRepository.save(product);
        }

        // 5. Cambiar estado del pedido a PAID
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 6. Crear el pago
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString());

        return paymentRepository.save(payment);
    }
}