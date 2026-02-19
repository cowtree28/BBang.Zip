package com.bbangzip.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Map<String, User> USERS = new ConcurrentHashMap<>();
    private static final Map<String, String> REFRESH_TOKENS = new ConcurrentHashMap<>();

    @GetMapping("/auth/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        return Map.of("username", username, "available", !USERS.containsKey(username));
    }

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> signup(@Valid @RequestBody SignUpRequest request) {
        if (!request.consent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "개인정보 동의가 필요합니다.");
        }
        if (USERS.containsKey(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.");
        }
        User user = new User(request.username(), request.password(), request.name(), request.birthDate(), request.gender(), request.accountNumber(), request.region());
        USERS.put(request.username(), user);
        return Map.of("username", user.username(), "name", user.name(), "region", user.region());
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        User user = USERS.get(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        String access = "access-" + UUID.randomUUID();
        String refresh = "refresh-" + UUID.randomUUID();
        REFRESH_TOKENS.put(refresh, user.username());
        return Map.of("accessToken", access, "refreshToken", refresh, "username", user.username());
    }

    @PostMapping("/auth/refresh")
    public Map<String, String> refresh(@RequestBody RefreshRequest request) {
        String username = REFRESH_TOKENS.get(request.refreshToken());
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다.");
        }
        return Map.of("accessToken", "access-" + UUID.randomUUID(), "username", username);
    }

    @GetMapping("/users/me")
    public UserProfile me(@RequestHeader("X-User-Id") String username) {
        User user = requireUser(username);
        return new UserProfile(user.username(), user.name(), user.birthDate(), user.gender(), user.accountNumber(), user.region());
    }

    @PatchMapping("/users/me")
    public UserProfile updateMe(@RequestHeader("X-User-Id") String username, @RequestBody UpdateProfileRequest request) {
        User user = requireUser(username);
        String nextRegion = request.region() == null || request.region().isBlank() ? user.region() : request.region();
        String nextName = request.name() == null || request.name().isBlank() ? user.name() : request.name();
        User updated = new User(user.username(), user.password(), nextName, user.birthDate(), user.gender(), user.accountNumber(), nextRegion);
        USERS.put(username, updated);
        return new UserProfile(updated.username(), updated.name(), updated.birthDate(), updated.gender(), updated.accountNumber(), updated.region());
    }

    @GetMapping("/auth/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    private User requireUser(String username) {
        User user = USERS.get(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        return user;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record SignUpRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String name,
            @Past LocalDate birthDate,
            @NotBlank String gender,
            @NotBlank String accountNumber,
            @NotBlank String region,
            boolean consent
    ) {}

    public record UpdateProfileRequest(String name, String region) {}

    private record User(String username, String password, String name, LocalDate birthDate,
                        String gender, String accountNumber, String region) {}

    public record UserProfile(String username, String name, LocalDate birthDate,
                              String gender, String accountNumber, String region) {}
}
