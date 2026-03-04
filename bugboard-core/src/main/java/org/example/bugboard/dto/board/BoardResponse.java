package org.example.bugboard.dto.board;

import org.example.bugboard.entity.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        Long usersId,
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
                board.getUsers().getId(),
                board.getUsers().getNickname(),
                board.getTitle(),
                board.getContent(),
                board.getViewCount(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
