package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardCreateRequest;
import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ForbiddenException;
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
    public Long create(Long usersId, BoardCreateRequest boardCreateRequest) {
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", usersId));
        Board board = Board.builder()
                .users(users)
                .title(boardCreateRequest.title())
                .content(boardCreateRequest.content())
                .build();
        return boardRepository.save(board).getId();
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
    public Board update(Long id, Long usersId, String title, String content) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
        if (!board.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 게시글만 수정할 수 있습니다.");
        }
        board.update(title, content);
        return board;
    }

    @Transactional
    public void delete(Long id, Long usersId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
        if (!board.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 게시글만 삭제할 수 있습니다.");
        }
        board.softDelete();
    }
}
