package com.ordersystem.api.exception;

import com.ordersystem.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 * Centraliza el manejo de errores y devuelve respuestas consistentes
 *
 * ACTUALIZADO: Agregados handlers específicos para excepciones de dominio
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========================================
    // VALIDACIÓN
    // ========================================

    /**
     * Maneja errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // ========================================
    // AUTENTICACIÓN Y AUTORIZACIÓN
    // ========================================

    /**
     * Maneja credenciales inválidas
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Email o contraseña incorrectos",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja usuario no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja acceso denegado (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "No tienes permisos para acceder a este recurso",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Maneja excepción custom ForbiddenException
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ========================================
    // EXCEPCIONES DE DOMINIO - 404 NOT FOUND
    // ========================================

    /**
     * Maneja producto no encontrado
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja pedido no encontrado
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ========================================
    // EXCEPCIONES DE DOMINIO - 400 BAD REQUEST
    // ========================================

    /**
     * Maneja stock insuficiente
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja producto inactivo
     */
    @ExceptionHandler(InactiveProductException.class)
    public ResponseEntity<ErrorResponse> handleInactiveProduct(
            InactiveProductException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================
    // EXCEPCIONES DE DOMINIO - 409 CONFLICT
    // ========================================

    /**
     * Maneja estado de pedido inválido
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderState(
            InvalidOrderStateException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ========================================
    // FALLBACK - EXCEPCIONES GENÉRICAS
    // ========================================

    /**
     * Maneja recursos no encontrados (fallback para RuntimeException genéricas)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        // Si el mensaje contiene "no encontrado", es un 404
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no encontrado")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Si contiene "no tienes permisos", es un 403
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no tienes permisos")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // Si contiene "stock", es un 400
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("stock")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Si contiene "inactive" o "inactivo", es un 400
        if (ex.getMessage() != null &&
                (ex.getMessage().toLowerCase().contains("inactive") ||
                        ex.getMessage().toLowerCase().contains("inactivo") ||
                        ex.getMessage().toLowerCase().contains("no está disponible"))) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Si contiene "must be paid" o "no puede ser" o similar, es un 409
        if (ex.getMessage() != null &&
                (ex.getMessage().toLowerCase().contains("must be paid") ||
                        ex.getMessage().toLowerCase().contains("debe estar pagado") ||
                        ex.getMessage().toLowerCase().contains("no se puede") ||
                        ex.getMessage().toLowerCase().contains("no puede ser"))) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Conflict",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // Cualquier otro RuntimeException es 500
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Maneja cualquier otra excepción no contemplada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        // IMPORTANTE: Loguear el error completo para debugging
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error inesperado",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}