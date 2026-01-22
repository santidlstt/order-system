package com.ordersystem.domain.model;

import com.ordersystem.domain.enums.PaymentStatus;
import com.ordersystem.domain.model.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de dominio para Pago
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long orderId;
    private Money amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;

    /**
     * Verificar si el pago fue aprobado
     */
    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }

    /**
     * Verificar si el pago está pendiente
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }
}