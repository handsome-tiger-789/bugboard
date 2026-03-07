package org.example.bugboard.repository;

import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.entity.Board;

import java.util.Optional;

public interface BoardRepository {

    Board save(Board board);

    Optional<Board> findById(Long id);

    BoardListResponse findBoards(int page, int size, String title);

}
