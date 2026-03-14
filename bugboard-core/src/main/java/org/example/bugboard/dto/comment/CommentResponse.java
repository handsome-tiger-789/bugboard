package org.example.bugboard.dto.comment;

import org.example.bugboard.entity.Comment;

import java.time.format.DateTimeFormatter;

public record CommentResponse(
        Long id,
        Long boardId,
        Long usersId,
        String nickname,
        String content,
        Integer likeCount,
        String createdAt,
        String updatedAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getUsers().getId(),
                comment.getUsers().getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt().format(FORMATTER),
                comment.getUpdatedAt().format(FORMATTER)
        );
    }
}
