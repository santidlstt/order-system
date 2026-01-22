package com.ordersystem.api.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para pagar un pedido
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequest {

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    private String paymentMethod;
}