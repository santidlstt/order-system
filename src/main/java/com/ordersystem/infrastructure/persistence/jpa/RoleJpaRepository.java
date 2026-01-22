package com.ordersystem.infrastructure.persistence.jpa;

import com.ordersystem.domain.enums.RoleType;
import com.ordersystem.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para RoleEntity
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleType name);
}
