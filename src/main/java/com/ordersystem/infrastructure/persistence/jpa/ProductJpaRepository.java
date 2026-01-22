package com.ordersystem.infrastructure.persistence.jpa;

import com.ordersystem.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para ProductEntity
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long>{
    List<ProductEntity> findByActiveTrue();
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
}
