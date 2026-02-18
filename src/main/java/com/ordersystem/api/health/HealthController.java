package com.ordersystem.api.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de health check para monitoreo de la aplicación.
 * Este endpoint es público (no requiere autenticación) para permitir
 * que plataformas como Render, load balancers o servicios de monitoreo
 * puedan verificar el estado del servicio.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * Endpoint de health check que retorna el estado de la aplicación.
     *
     * @return ResponseEntity con información del estado del servicio
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "order-system");
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health);
    }
}