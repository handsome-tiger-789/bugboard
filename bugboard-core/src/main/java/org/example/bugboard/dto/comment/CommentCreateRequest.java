package org.example.bugboard.dto.comment;

public record CommentCreateRequest(
        Long userId,
        String content
) {
}
