package com.ordersystem.infrastructure.mapper;

import com.ordersystem.domain.model.Payment;
import com.ordersystem.domain.model.valueobject.Money;
import com.ordersystem.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre Payment (dominio) y PaymentEntity (JPA)
 */
@Component
public class PaymentMapper {

    /**
     * Convierte PaymentEntity a Payment (dominio)
     */
    public Payment toDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(entity.getId());
        payment.setOrderId(entity.getOrderId());
        payment.setAmount(new Money(entity.getAmount(), entity.getCurrency()));
        payment.setStatus(entity.getStatus());
        payment.setPaymentMethod(entity.getPaymentMethod());
        payment.setTransactionId(entity.getTransactionId());

        return payment;
    }

    /**
     * Convierte Payment (dominio) a PaymentEntity (JPA)
     */
    public PaymentEntity toEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId());
        entity.setOrderId(payment.getOrderId());
        entity.setAmount(payment.getAmount().getAmount());
        entity.setCurrency(payment.getAmount().getCurrency());
        entity.setStatus(payment.getStatus());
        entity.setPaymentMethod(payment.getPaymentMethod());
        entity.setTransactionId(payment.getTransactionId());

        return entity;
    }
}