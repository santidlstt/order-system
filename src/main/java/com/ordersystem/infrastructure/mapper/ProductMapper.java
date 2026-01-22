package com.ordersystem.infrastructure.mapper;

import com.ordersystem.domain.model.Product;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre Product (dominio) y ProductEntity (JPA)
 */
@Component
public class ProductMapper {
    /**
     * Convierte ProductEntity a Product (dominio)
     */
    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        Product product  = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(new Money(entity.getPriceAmount(), entity.getPriceCurrency()));
        product.setStock(entity.getStock());
        product.setActive(entity.getActive());

        return product;
    }

    /**
     *  Convierte de Product (dominio) a ProductEntity (JPA)
     */
    public ProductEntity toEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPriceAmount(product.getPrice().getAmount());
        entity.setPriceCurrency(product.getPrice().getCurrency());
        entity.setStock(product.getStock());
        entity.setActive(product.getActive());

        return entity;
    }

    /**
     * Actualiza una entidad existente con datos del dominio
     */
    public void updateEntity(ProductEntity entity, Product product) {
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPriceAmount(product.getPrice().getAmount());
        entity.setPriceCurrency(product.getPrice().getCurrency());
        entity.setStock(product.getStock());
        entity.setActive(product.getActive());
    }

}
