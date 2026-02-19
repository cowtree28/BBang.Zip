package com.bbangzip.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1000);
    private static final Map<String, Order> ORDERS = new ConcurrentHashMap<>();
    private final OrderIntegrationService integrationService;

    public OrderController(OrderIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody CreateOrderRequest request) {
        Map product = integrationService.fetchProduct(request.productId());
        if (product == null || product.get("id") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 상품입니다.");
        }

        String orderId = "ORD-" + ID_SEQUENCE.incrementAndGet();
        Order order = new Order(orderId, userId, request.productId(), request.productName(), request.quantity(), request.priceSnapshot(),
                request.pickupLocation(), OffsetDateTime.now().plusHours(2), "WAITING_PAYMENT");
        ORDERS.put(orderId, order);

        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("productName", product.get("name"));

        if (request.quantity() >= 3) {
            String queueId = "product-" + request.productId();
            response.put("queue", integrationService.enrollQueueIfNeeded(userId, queueId));
        }
        return response;
    }

    @GetMapping("/orders/{orderId}")
    public Order get(@PathVariable String orderId) {
        Order order = ORDERS.get(orderId);
        if (order == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
        return order;
    }

    @GetMapping("/users/me/orders")
    public List<Order> myOrders(@RequestHeader("X-User-Id") String userId) {
        return ORDERS.values().stream().filter(order -> order.userId().equals(userId)).toList();
    }

    public record CreateOrderRequest(
            @NotNull Long productId,
            @NotBlank String productName,
            @NotNull @Min(1) Integer quantity,
            @NotNull @Min(100) Integer priceSnapshot,
            @NotBlank String pickupLocation
    ) {}

    public record Order(String orderId, String userId, long productId, String productName, int quantity,
                        int priceSnapshot, String pickupLocation, OffsetDateTime pickupAt, String status) {}
}
