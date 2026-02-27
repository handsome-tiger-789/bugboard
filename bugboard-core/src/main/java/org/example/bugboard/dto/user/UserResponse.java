package org.example.bugboard.dto.user;

import org.example.bugboard.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
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
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getProvider(),
                user.getProviderId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
