package com.ordersystem.domain.repository;

import com.ordersystem.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para Order (capa de dominio)
 */

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByUserId(Long userId);
    boolean existsById(Long id);
}
