package org.example.bugboard.dto.comment;

import org.example.bugboard.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long boardId,
        Long usersId,
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
                comment.getUsers().getId(),
                comment.getUsers().getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
