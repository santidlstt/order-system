package com.ordersystem.application.product;

import com.ordersystem.api.product.dto.CreateProductRequest;
import com.ordersystem.api.product.dto.ProductResponse;
import com.ordersystem.api.product.dto.UpdateProductRequest;
import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de productos
 */

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * Crear un nuevo producto
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(new Money(request.getPrice(), request.getCurrency()));
        product.setStock(request.getStock());
        product.setActive(request.getActive());

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    /**
     * Obtener todos los productos
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener solo productos activos
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findActiveProducts().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return mapToResponse(product);
    }

    /**
     * Actualizar producto existente
     */
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(new Money(request.getPrice(), request.getCurrency()));
        product.setStock(request.getStock());
        product.setActive(request.getActive());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    /**
     * Eliminar producto (soft delete - marca como inactivo)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Mapea Product a ProductResponse
     */
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice().getAmount());
        response.setCurrency(product.getPrice().getCurrency());
        response.setStock(product.getStock());
        response.setActive(product.getActive());

        return response;
    }


}
