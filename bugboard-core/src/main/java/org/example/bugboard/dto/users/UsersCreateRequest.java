package org.example.bugboard.dto.users;

public record UsersCreateRequest(
        String provider,
        String providerId,
        String email,
        String name,
        String nickname,
        String role
) {
}
