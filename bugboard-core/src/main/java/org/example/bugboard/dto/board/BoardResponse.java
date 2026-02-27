package org.example.bugboard.dto.board;

import org.example.bugboard.entity.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        Long userId,
        String nickname,
        String title,
        String content,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getTitle(),
                board.getContent(),
                board.getViewCount(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
