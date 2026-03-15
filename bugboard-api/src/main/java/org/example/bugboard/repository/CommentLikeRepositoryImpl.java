package org.example.bugboard.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.CommentLike;
import org.example.bugboard.entity.QCommentLike;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QCommentLike commentLike = QCommentLike.commentLike;

    @Override
    public Optional<CommentLike> findByCommentIdAndUsersId(Long commentId, Long usersId) {
        CommentLike result = queryFactory
                .selectFrom(commentLike)
                .where(
                        commentLike.comment.id.eq(commentId),
                        commentLike.users.id.eq(usersId)
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByCommentIdAndUsersId(Long commentId, Long usersId) {
        return queryFactory
                .selectOne()
                .from(commentLike)
                .where(
                        commentLike.comment.id.eq(commentId),
                        commentLike.users.id.eq(usersId)
                )
                .fetchFirst() != null;
    }

    @Override
    public long countByCommentId(Long commentId) {
        Long count = queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public void deleteByCommentId(Long commentId) {
        queryFactory
                .delete(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .execute();
    }

    @Override
    public CommentLike save(CommentLike entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(CommentLike entity) {
        entityManager.remove(entity);
    }

    @Override
    public Map<Long, Long> countByBoardId(Long boardId) {
        List<Tuple> results = queryFactory
                .select(commentLike.comment.id, commentLike.count())
                .from(commentLike)
                .where(commentLike.comment.board.id.eq(boardId))
                .groupBy(commentLike.comment.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(commentLike.comment.id),
                        tuple -> tuple.get(commentLike.count())
                ));
    }

    @Override
    public Set<Long> findLikedCommentIdsByBoardIdAndUsersId(Long boardId, Long usersId) {
        List<Long> result = queryFactory
                .select(commentLike.comment.id)
                .from(commentLike)
                .where(
                        commentLike.comment.board.id.eq(boardId),
                        commentLike.users.id.eq(usersId)
                )
                .fetch();

        return new HashSet<>(result);
    }
}
