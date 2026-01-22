package com.ordersystem.infrastructure.persistence.jpa;

import com.ordersystem.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para PaymentEntity
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity,Long> {
    Optional<PaymentEntity> findByOrderId(Long orderId);
    boolean existsByOrderId(Long orderId);
}
