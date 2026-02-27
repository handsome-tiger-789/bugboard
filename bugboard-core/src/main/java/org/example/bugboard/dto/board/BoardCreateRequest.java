package org.example.bugboard.dto.board;

public record BoardCreateRequest(
        Long userId,
        String title,
        String content
) {
}
