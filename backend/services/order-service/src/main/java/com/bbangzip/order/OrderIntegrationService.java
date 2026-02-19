package com.bbangzip.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OrderIntegrationService {

    private final RestClient restClient;
    private final String productBaseUrl;
    private final String queueBaseUrl;

    public OrderIntegrationService(RestClient.Builder builder,
                                   @Value("${services.product.base-url}") String productBaseUrl,
                                   @Value("${services.queue.base-url}") String queueBaseUrl) {
        this.restClient = builder.build();
        this.productBaseUrl = productBaseUrl;
        this.queueBaseUrl = queueBaseUrl;
    }

    public Map fetchProduct(Long productId) {
        return restClient.get()
                .uri(productBaseUrl + "/api/v1/products/" + productId)
                .retrieve()
                .body(Map.class);
    }

    public Map enrollQueueIfNeeded(String userId, String queueId) {
        return restClient.post()
                .uri(queueBaseUrl + "/api/v1/queue/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("queueId", queueId, "userId", userId))
                .retrieve()
                .body(Map.class);
    }
}
