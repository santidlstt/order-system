package com.ordersystem.api.auth;

import com.ordersystem.api.BaseIntegrationTest;
import com.ordersystem.api.auth.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para AuthController.
 *
 * Cobertura:
 * 1. Login exitoso con credenciales válidas (USER)
 * 2. Login exitoso con credenciales válidas (ADMIN)
 * 3. Login fallido por credenciales inválidas
 * 4. Login fallido por usuario inexistente
 */
@DisplayName("AuthController - Integration Tests")
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /api/auth/login - Login exitoso como USER retorna JWT")
    void login_ValidCredentials_AsUser_ReturnsJwtToken() throws Exception {
        // Given: Credenciales válidas de user@test.com
        LoginRequest request = new LoginRequest("user@test.com", "1234");

        // When: POST /api/auth/login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then: Status 200 OK con JWT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Login exitoso como ADMIN retorna JWT con múltiples roles")
    void login_ValidCredentials_AsAdmin_ReturnsJwtToken() throws Exception {
        // Given: Credenciales válidas de admin@test.com
        LoginRequest request = new LoginRequest("admin@test.com", "1234");

        // When: POST /api/auth/login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then: Status 200 OK con JWT y roles USER + ADMIN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles.length()").value(2)); // ROLE_USER + ROLE_ADMIN
    }

    @Test
    @DisplayName("POST /api/auth/login - Login con contraseña incorrecta retorna 401")
    void login_InvalidPassword_ReturnsUnauthorized() throws Exception {
        // Given: Credenciales con contraseña incorrecta
        LoginRequest request = new LoginRequest("user@test.com", "wrongpassword");

        // When & Then: 401 Unauthorized
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Login con email inexistente retorna 401")
    void login_NonExistentUser_ReturnsUnauthorized() throws Exception {
        // Given: Credenciales con email que no existe
        LoginRequest request = new LoginRequest("nonexistent@test.com", "1234");

        // When & Then: 401 Unauthorized
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Request sin email retorna 400")
    void login_MissingEmail_ReturnsBadRequest() throws Exception {
        // Given: Request sin email
        String invalidRequest = "{\"password\":\"1234\"}";

        // When & Then: 400 Bad Request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Request sin password retorna 400")
    void login_MissingPassword_ReturnsBadRequest() throws Exception {
        // Given: Request sin password
        String invalidRequest = "{\"email\":\"user@test.com\"}";

        // When & Then: 400 Bad Request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Email con formato inválido retorna 400")
    void login_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Given: Email sin formato válido
        LoginRequest request = new LoginRequest("notanemail", "1234");

        // When & Then: 400 Bad Request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}