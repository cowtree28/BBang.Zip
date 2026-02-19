package com.bbangzip.queue;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {

    @GetMapping("/{queueId}")
    public Map<String, Object> position(@PathVariable String queueId) {
        return Map.of("queueId", queueId, "position", 12, "estimatedWaitMinutes", 24);
    }

    @GetMapping(value = "/{queueId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> stream(@PathVariable String queueId) {
        return Flux.interval(Duration.ofSeconds(2))
                .take(5)
                .map(i -> Map.of(
                        "queueId", queueId,
                        "position", Math.max(1, 12 - i.intValue()),
                        "estimatedWaitMinutes", Math.max(2, 24 - (i.intValue() * 2))
                ));
    }
}
