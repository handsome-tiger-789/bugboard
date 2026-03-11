package org.example.bugboard.dto.board;

import java.util.List;

public record BoardListResponse(
        List<BoardResponse> boards,
        int page,
        int size,
        long totalCount,
        int totalPages
) {
}
