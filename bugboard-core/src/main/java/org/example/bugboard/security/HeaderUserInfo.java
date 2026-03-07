package org.example.bugboard.security;

public record HeaderUserInfo(
        Long userId,
        String userName,
        String userEmail
) {
}
