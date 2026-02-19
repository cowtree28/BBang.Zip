package com.bbangzip.queue;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1/queue")
@CrossOrigin(origins = "*")
public class QueueController {

    private static final Map<String, CopyOnWriteArrayList<String>> QUEUES = new ConcurrentHashMap<>();

    @PostMapping("/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> enroll(@RequestBody EnrollRequest request) {
        String queueId = request.queueId();
        CopyOnWriteArrayList<String> queue = QUEUES.computeIfAbsent(queueId, id -> new CopyOnWriteArrayList<>());
        if (!queue.contains(request.userId())) {
            queue.add(request.userId());
        }
        int position = queue.indexOf(request.userId()) + 1;
        return Map.of("queueId", queueId, "userId", request.userId(), "position", position);
    }

    @GetMapping("/{queueId}")
    public Map<String, Object> position(@PathVariable String queueId, @RequestParam String userId) {
        CopyOnWriteArrayList<String> queue = requireQueue(queueId);
        int position = queue.indexOf(userId) + 1;
        if (position == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대기열에 사용자가 없습니다.");
        return Map.of("queueId", queueId, "position", position, "estimatedWaitMinutes", Math.max(1, position * 2));
    }

    @PostMapping("/{queueId}/leave")
    public Map<String, Object> leave(@PathVariable String queueId, @RequestBody LeaveRequest request) {
        CopyOnWriteArrayList<String> queue = requireQueue(queueId);
        boolean removed = queue.remove(request.userId());
        return Map.of("queueId", queueId, "removed", removed, "remaining", queue.size());
    }

    @PostMapping("/{queueId}/promote")
    public Map<String, Object> promote(@PathVariable String queueId) {
        CopyOnWriteArrayList<String> queue = requireQueue(queueId);
        if (queue.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대기열이 비어 있습니다.");
        String userId = queue.remove(0);
        return Map.of("queueId", queueId, "promotedUser", userId, "remaining", queue.size());
    }

    @GetMapping(value = "/{queueId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> stream(@PathVariable String queueId, @RequestParam String userId) {
        return Flux.interval(Duration.ofSeconds(2))
                .map(tick -> {
                    CopyOnWriteArrayList<String> queue = QUEUES.computeIfAbsent(queueId, id -> new CopyOnWriteArrayList<>());
                    int position = queue.indexOf(userId) + 1;
                    if (position < 0) position = 0;
                    return Map.of("queueId", queueId, "position", position, "estimatedWaitMinutes", Math.max(1, position * 2));
                });
    }

    @GetMapping("/{queueId}/snapshot")
    public List<String> snapshot(@PathVariable String queueId) {
        return List.copyOf(requireQueue(queueId));
    }

    private CopyOnWriteArrayList<String> requireQueue(String queueId) {
        CopyOnWriteArrayList<String> queue = QUEUES.get(queueId);
        if (queue == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대기열을 찾을 수 없습니다.");
        return queue;
    }

    public record EnrollRequest(@NotBlank String queueId, @NotBlank String userId) {}

    public record LeaveRequest(@NotBlank String userId) {}
}
