package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.User;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.BoardRepository;
import org.example.bugboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Board create(Long userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Board board = Board.builder()
                .user(user)
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

    public List<Board> findAll() {
        return boardRepository.findAll();
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
