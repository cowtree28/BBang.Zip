package com.bbangzip.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    public Map<String, Object> create(@RequestBody CreateOrderRequest request) {
        return Map.of(
                "orderId", "ORD-" + System.currentTimeMillis(),
                "productId", request.productId(),
                "quantity", request.quantity(),
                "pickupAt", OffsetDateTime.now().plusHours(2).toString(),
                "status", "WAITING_PAYMENT"
        );
    }

    public record CreateOrderRequest(@NotNull Long productId, @NotNull Integer quantity) {}
}
