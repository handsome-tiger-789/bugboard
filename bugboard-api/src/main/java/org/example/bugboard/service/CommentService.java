package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Comment;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.BoardRepository;
import org.example.bugboard.repository.CommentRepository;
import org.example.bugboard.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public Comment create(Long boardId, Long usersId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", boardId));
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", usersId));
        Comment comment = Comment.builder()
                .board(board)
                .users(users)
                .content(content)
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> findByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    @Transactional
    public Comment update(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        comment.update(content);
        return comment;
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        comment.softDelete();
    }
}
