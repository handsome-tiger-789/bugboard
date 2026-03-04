package org.example.bugboard.dto.comment;

public record CommentCreateRequest(
        Long usersId,
        String content
) {
}
