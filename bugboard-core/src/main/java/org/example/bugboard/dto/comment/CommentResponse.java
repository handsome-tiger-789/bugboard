package org.example.bugboard.dto.comment;

import org.example.bugboard.entity.Comment;

import java.time.format.DateTimeFormatter;

public record CommentResponse(
        Long id,
        Long boardId,
        Long usersId,
        String nickname,
        String content,
        Long likeCount,
        Boolean liked,
        String createdAt,
        String updatedAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentResponse from(Comment comment, Long likeCount, Boolean liked) {
        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getUsers().getId(),
                comment.getUsers().getNickname(),
                comment.getContent(),
                likeCount,
                liked,
                comment.getCreatedAt().format(FORMATTER),
                comment.getUpdatedAt().format(FORMATTER)
        );
    }
}
