package org.example.bugboard.dto.user;

public record UserCreateRequest(
        String provider,
        String providerId,
        String email,
        String name,
        String nickname,
        String role
) {
}
