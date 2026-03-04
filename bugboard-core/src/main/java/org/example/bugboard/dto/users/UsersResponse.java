package org.example.bugboard.dto.users;

import org.example.bugboard.entity.Users;

import java.time.LocalDateTime;

public record UsersResponse(
        Long id,
        String provider,
        String providerId,
        String email,
        String name,
        String nickname,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UsersResponse from(Users users) {
        return new UsersResponse(
                users.getId(),
                users.getProvider(),
                users.getProviderId(),
                users.getEmail(),
                users.getName(),
                users.getNickname(),
                users.getRole(),
                users.getCreatedAt(),
                users.getUpdatedAt()
        );
    }
}
