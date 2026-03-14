package org.example.bugboard.dto.board;

import org.example.bugboard.entity.Board;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record BoardDetailResponse(
        Long id,
        Long usersId,
        String nickname,
        String title,
        String content,
        Integer viewCount,
        String createdAt,
        String updatedAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static BoardDetailResponse from(Board board) {
        return new BoardDetailResponse(
                board.getId(),
                board.getUsers().getId(),
                board.getUsers().getNickname(),
                board.getTitle(),
                board.getContent(),
                board.getViewCount(),
                board.getCreatedAt().format(FORMATTER),
                board.getUpdatedAt().format(FORMATTER)
        );
    }
}
