package org.example.bugboard.dto.board;

public record BoardCreateRequest(
        Long usersId,
        String title,
        String content
) {
}
