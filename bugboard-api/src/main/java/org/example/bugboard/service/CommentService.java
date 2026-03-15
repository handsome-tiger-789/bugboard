package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.comment.CommentResponse;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Comment;
import org.example.bugboard.entity.CommentLike;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ForbiddenException;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.BoardRepository;
import org.example.bugboard.repository.CommentLikeRepository;
import org.example.bugboard.repository.CommentRepository;
import org.example.bugboard.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
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

    public List<CommentResponse> findByBoardId(Long boardId, Long usersId) {
        List<Comment> comments = commentRepository.findByBoardIdAndIsDeleteFalseOrderByCreatedAtDesc(boardId);

        Map<Long, Long> likeCountMap = commentLikeRepository.countByBoardId(boardId);

        Set<Long> likedCommentIds = commentLikeRepository.findLikedCommentIdsByBoardIdAndUsersId(boardId, usersId);

        return comments.stream()
                .map(comment -> CommentResponse.from(
                        comment,
                        likeCountMap.getOrDefault(comment.getId(), 0L),
                        likedCommentIds.contains(comment.getId())))
                .toList();
    }

    @Transactional
    public CommentResponse update(Long id, Long usersId, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        if (!comment.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 댓글만 수정할 수 있습니다.");
        }
        comment.update(content);
        boolean liked = commentLikeRepository.existsByCommentIdAndUsersId(id, usersId);
        return CommentResponse.from(comment, commentLikeRepository.countByCommentId(id), liked);
    }

    @Transactional
    public void toggleLike(Long id, Long usersId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));

        commentLikeRepository.findByCommentIdAndUsersId(id, usersId)
                .ifPresentOrElse(
                        commentLikeRepository::delete,
                        () -> {
                            Users users = usersRepository.findById(usersId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Users", usersId));
                            commentLikeRepository.save(CommentLike.builder()
                                    .comment(comment)
                                    .users(users)
                                    .build());
                        }
                );
    }

    @Transactional
    public void delete(Long id, Long usersId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", id));
        if (!comment.getUsers().getId().equals(usersId)) {
            throw new ForbiddenException("본인의 댓글만 삭제할 수 있습니다.");
        }
        commentLikeRepository.deleteByCommentId(id);
        comment.softDelete();
    }

}
