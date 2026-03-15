package org.example.bugboard.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "내용이 비어있습니다.")
        String content
) {
}
