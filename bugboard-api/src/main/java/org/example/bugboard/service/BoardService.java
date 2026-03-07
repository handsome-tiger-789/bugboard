package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.BoardRepository;
import org.example.bugboard.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private static final int PAGE_SIZE = 5;

    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public Board create(Long usersId, String title, String content) {
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", usersId));
        Board board = Board.builder()
                .users(users)
                .title(title)
                .content(content)
                .build();
        return boardRepository.save(board);
    }

    @Transactional
    public Board findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
        board.increaseViewCount();
        return board;
    }

    public BoardListResponse findAll(int page, String title) {
        return boardRepository.findBoards(page, PAGE_SIZE, title);
    }

    @Transactional
    public Board update(Long id, String title, String content) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
        board.update(title, content);
        return board;
    }

    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
        board.softDelete();
    }
}
