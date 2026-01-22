package com.ordersystem.domain.repository;

import com.ordersystem.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para Product (capa de dominio)
 * Sin dependencias de frameworks
 */

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findActiveProducts();
    void deleteById(Long id);
    boolean existsById(Long id);
}
