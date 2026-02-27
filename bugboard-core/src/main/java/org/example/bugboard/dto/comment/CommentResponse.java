package org.example.bugboard.dto.comment;

import org.example.bugboard.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long boardId,
        Long userId,
        String nickname,
        String content,
        Integer likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
