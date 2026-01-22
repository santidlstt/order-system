package com.ordersystem.infrastructure.persistence.jpa;

import com.ordersystem.domain.model.Order;
import com.ordersystem.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para OrderEntity
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long>{
    List<OrderEntity> findByUserId(Long userId);
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    OrderEntity findByIdWithItems(Long id);
}
