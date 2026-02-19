package com.bbangzip.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Map<String, PaymentResult> PAYMENTS = new ConcurrentHashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResult pay(@Valid @RequestBody PaymentRequest request) {
        String paymentId = "PAY-" + UUID.randomUUID();
        PaymentResult result = new PaymentResult(paymentId, request.orderId(), "PAID", OffsetDateTime.now());
        PAYMENTS.put(paymentId, result);
        return result;
    }

    @GetMapping("/{paymentId}")
    public PaymentResult get(@PathVariable String paymentId) {
        return PAYMENTS.getOrDefault(paymentId, new PaymentResult(paymentId, "UNKNOWN", "NOT_FOUND", OffsetDateTime.now()));
    }

    public record PaymentRequest(@NotBlank String orderId, @NotBlank String cardNo, @NotBlank String cardPassword) {}

    public record PaymentResult(String paymentId, String orderId, String status, OffsetDateTime paidAt) {}
}
