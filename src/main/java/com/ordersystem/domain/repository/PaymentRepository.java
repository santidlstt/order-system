package com.ordersystem.domain.repository;

import com.ordersystem.domain.model.Payment;

import java.util.Optional;

/**
 * Interfaz de repositorio para Payment (capa de dominio)
 */

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByOrderId(Long orderId);
    boolean existsByOrderId(Long orderId);
}
