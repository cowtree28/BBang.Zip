package com.bbangzip.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private static final AtomicLong ID_SEQUENCE = new AtomicLong(2);
    private static final Map<Long, Product> PRODUCTS = new ConcurrentHashMap<>();

    static {
        PRODUCTS.put(1L, new Product(1L, "소금빵", "겉바속촉 대표 메뉴", 3500, 120, "대전 중구", 4.8, true, false));
        PRODUCTS.put(2L, new Product(2L, "명란바게트", "짭짤한 시그니처", 4200, 48, "대전 중구", 4.6, true, false));
    }

    @GetMapping
    public Map<String, Object> products(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String region,
                                        @RequestParam(required = false) Double minRating) {
        List<Product> filtered = PRODUCTS.values().stream()
                .filter(p -> !p.deleted())
                .filter(p -> keyword == null || p.name().contains(keyword) || p.description().contains(keyword))
                .filter(p -> region == null || p.region().contains(region))
                .filter(p -> minRating == null || p.rating() >= minRating)
                .sorted(Comparator.comparing(Product::mainBread).reversed().thenComparing(Product::rating).reversed())
                .toList();

        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        return Map.of(
                "page", page,
                "size", size,
                "total", filtered.size(),
                "items", filtered.subList(from, to)
        );
    }

    @GetMapping("/{productId}")
    public Product detail(@PathVariable Long productId) {
        Product product = PRODUCTS.get(productId);
        if (product == null || product.deleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return product;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody ProductUpsertRequest request) {
        validateMainBreadLimit(request.mainBread());
        long id = ID_SEQUENCE.incrementAndGet();
        Product product = new Product(id, request.name(), request.description(), request.price(), request.stock(), request.region(), 0.0, request.mainBread(), false);
        PRODUCTS.put(id, product);
        return product;
    }

    @PatchMapping("/{productId}")
    public Product update(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest request) {
        Product origin = detail(productId);
        boolean nextMain = request.mainBread() == null ? origin.mainBread() : request.mainBread();
        if (!origin.mainBread() && nextMain) validateMainBreadLimit(true);

        Product updated = new Product(
                origin.id(),
                request.name() == null ? origin.name() : request.name(),
                request.description() == null ? origin.description() : request.description(),
                request.price() == null ? origin.price() : request.price(),
                request.stock() == null ? origin.stock() : request.stock(),
                request.region() == null ? origin.region() : request.region(),
                origin.rating(),
                nextMain,
                origin.deleted()
        );
        PRODUCTS.put(origin.id(), updated);
        return updated;
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long productId) {
        Product product = detail(productId);
        PRODUCTS.put(productId, new Product(product.id(), product.name(), product.description(), product.price(), 0,
                product.region(), product.rating(), product.mainBread(), true));
    }

    private void validateMainBreadLimit(boolean willMainBread) {
        if (!willMainBread) return;
        long mainCount = PRODUCTS.values().stream().filter(Product::mainBread).filter(p -> !p.deleted()).count();
        if (mainCount >= 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "주빵은 최대 3개까지 설정 가능합니다.");
        }
    }

    public record Product(long id, String name, String description, int price, int stock, String region,
                          double rating, boolean mainBread, boolean deleted) {}

    public record ProductUpsertRequest(
            @NotBlank String name,
            @NotBlank String description,
            @NotNull @Min(100) Integer price,
            @NotNull @Min(0) Integer stock,
            @NotBlank String region,
            boolean mainBread
    ) {}

    public record ProductUpdateRequest(
            String name,
            String description,
            @Min(100) Integer price,
            @Min(0) Integer stock,
            String region,
            Boolean mainBread
    ) {}
}
