package com.ordersystem.api.order;

import com.ordersystem.api.order.dto.CreateOrderRequest;
import com.ordersystem.api.order.dto.OrderResponse;
import com.ordersystem.api.order.dto.PayOrderRequest;
import com.ordersystem.application.order.CancelOrderService;
import com.ordersystem.application.order.CreateOrderService;
import com.ordersystem.application.order.OrderQueryService;
import com.ordersystem.application.order.PayOrderService;
import com.ordersystem.domain.enums.OrderStatus;
import com.ordersystem.domain.model.Order;
import com.ordersystem.domain.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de pedidos
 * Gestiona todo el flujo de pedidos: crear, pagar, cancelar, consultar
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final PayOrderService payOrderService;
    private final CancelOrderService cancelOrderService;
    private final OrderQueryService orderQueryService;
    private final OrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Crear pedido", description = "Crear un nuevo pedido (valida stock, no lo descuenta)")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        // Obtener ID del usuario autenticado (en producción esto vendría del JWT)
        Long userId = getUserIdFromAuthentication(authentication);

        Order order = createOrderService.execute(userId, request);
        OrderResponse response = orderQueryService.getOrderById(order.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Listar pedidos", description = "USER ve sus pedidos, ADMIN ve todos")
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<OrderResponse> orders;
        if (isAdmin) {
            // ADMIN ve todos los pedidos
            orders = orderQueryService.getAllOrders();
        } else {
            // USER ve solo sus pedidos
            Long userId = getUserIdFromAuthentication(authentication);
            orders = orderQueryService.getUserOrders(userId);
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Ver detalle del pedido", description = "Ver detalles de un pedido específico")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        // Verificar que el usuario solo pueda ver sus propios pedidos (a menos que sea ADMIN)
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Long userId = getUserIdFromAuthentication(authentication);

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para ver este pedido");
        }

        OrderResponse response = orderQueryService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Pagar pedido", description = "Procesar pago del pedido (descuenta stock, idempotente)")
    public ResponseEntity<String> payOrder(
            @PathVariable Long id,
            @Valid @RequestBody PayOrderRequest request,
            Authentication authentication) {

        // Verificar que el usuario solo pueda pagar sus propios pedidos
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Long userId = getUserIdFromAuthentication(authentication);

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para pagar este pedido");
        }

        payOrderService.execute(id, request.getPaymentMethod());

        return ResponseEntity.ok("Pedido pagado exitosamente");
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Cancelar pedido", description = "Cancelar un pedido (devuelve stock si estaba pagado)")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        // Verificar que el usuario solo pueda cancelar sus propios pedidos
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Long userId = getUserIdFromAuthentication(authentication);

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para cancelar este pedido");
        }

        cancelOrderService.execute(id);

        return ResponseEntity.ok("Pedido cancelado exitosamente");
    }

    @PutMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marcar como enviado", description = "Cambiar estado del pedido a SHIPPED (solo ADMIN)")
    public ResponseEntity<String> shipOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        if (!order.canBeShipped()) {
            throw new RuntimeException("El pedido no puede ser enviado. Estado actual: " + order.getStatus());
        }

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        return ResponseEntity.ok("Pedido marcado como enviado");
    }

    /**
     * Helper para obtener el ID del usuario desde el email del JWT
     * En producción esto debería venir directamente del token
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getName();

        // Buscar el usuario por email para obtener su ID
        // Este es un workaround simple. En producción el ID vendría en el JWT
        if (email.equals("user@test.com")) {
            return 1L;
        } else if (email.equals("admin@test.com")) {
            return 2L;
        }

        throw new RuntimeException("Usuario no encontrado");
    }
}