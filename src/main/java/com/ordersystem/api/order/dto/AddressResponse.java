package com.ordersystem.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para dirección
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private String street;
    private String city;
    private String country;
}