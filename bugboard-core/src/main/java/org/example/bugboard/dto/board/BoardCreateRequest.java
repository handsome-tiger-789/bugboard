package org.example.bugboard.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardCreateRequest(
        @NotBlank(message = "제목이 비어있습니다.")
        @Size(max = 50, message = "제목은 50자 이하로 입력해주세요.")
        String title,

        @NotBlank(message = "내용이 비어있습니다.")
        String content
) {
}
