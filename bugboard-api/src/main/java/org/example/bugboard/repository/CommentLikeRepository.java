package org.example.bugboard.repository;

import org.example.bugboard.entity.CommentLike;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CommentLikeRepository {

    Optional<CommentLike> findByCommentIdAndUsersId(Long commentId, Long usersId);

    boolean existsByCommentIdAndUsersId(Long commentId, Long usersId);

    long countByCommentId(Long commentId);

    void deleteByCommentId(Long commentId);

    CommentLike save(CommentLike commentLike);

    void delete(CommentLike commentLike);

    Map<Long, Long> countByBoardId(Long boardId);

    Set<Long> findLikedCommentIdsByBoardIdAndUsersId(Long boardId, Long usersId);
}
