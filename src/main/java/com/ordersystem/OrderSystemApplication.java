package com.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Order System - Backend Application
 * Sistema de gestión de pedidos con arquitectura limpia
 *
 * @author Santiago de los Santos
 */
@SpringBootApplication
@EnableJpaAuditing
public class OrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSystemApplication.class, args);
    }

}
