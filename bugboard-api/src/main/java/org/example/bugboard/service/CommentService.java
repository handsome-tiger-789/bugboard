package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Comment;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ForbiddenException;
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
    public Long create(Long boardId, Long usersId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", boardId));
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", usersId));
        Comment comment = Comment.builder()
                .board(board)
                .users(users)
                .content(content)
                .build();
        return commentRepository.save(comment).getId();
    }

    public List<Comment> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdAndIsDeleteFalseOrderByCreatedAtDesc(boardId);
    }

    @Transactional
    public Comment update(Long id, Long usersId, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        if (!comment.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 댓글만 수정할 수 있습니다.");
        }
        comment.update(content);
        return comment;
    }

    @Transactional
    public void like(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        comment.increaseLikeCount();
    }

    @Transactional
    public void delete(Long id, Long usersId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        if (!comment.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 댓글만 삭제할 수 있습니다.");
        }
        comment.softDelete();
    }
}
