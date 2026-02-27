package org.example.bugboard.dto.board;

public record BoardUpdateRequest(
        String title,
        String content
) {
}
