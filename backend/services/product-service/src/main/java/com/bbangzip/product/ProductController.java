package com.bbangzip.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping
    public Map<String, Object> products(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String keyword) {
        return Map.of(
                "page", page,
                "size", size,
                "keyword", keyword,
                "items", List.of(
                        Map.of("id", 1, "name", "소금빵", "price", 3500, "stock", 120),
                        Map.of("id", 2, "name", "명란바게트", "price", 4200, "stock", 48)
                )
        );
    }
}
