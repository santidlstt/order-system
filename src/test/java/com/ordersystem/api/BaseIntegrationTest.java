package com.ordersystem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase base para tests de integración.
 *
 * Configuración:
 * - @SpringBootTest: Levanta el contexto completo de Spring
 * - @AutoConfigureMockMvc: Configura MockMvc automáticamente
 * - @ActiveProfiles("test"): Usa application-test.yml
 * - @Transactional: Hace rollback después de cada test
 * - @Sql: Ejecuta test-data.sql antes de cada test
 *
 * Proporciona:
 * - mockMvc: Para simular peticiones HTTP
 * - objectMapper: Para serializar/deserializar JSON
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Hook para configuración adicional en clases hijas
        // Por defecto no hace nada
    }
}