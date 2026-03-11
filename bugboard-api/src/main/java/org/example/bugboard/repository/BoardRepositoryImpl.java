package org.example.bugboard.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.dto.board.BoardResponse;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.QBoard;
import org.example.bugboard.entity.QUsers;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QBoard board = QBoard.board;
    private static final QUsers users = QUsers.users;

    @Override
    public Board save(Board entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Optional<Board> findById(Long id) {
        Board result = queryFactory
                .selectFrom(board)
                .join(board.users, users).fetchJoin()
                .where(board.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public BoardListResponse findBoards(int page, int size, String title) {
        BooleanExpression condition = board.isDelete.eq(false);

        if (title != null && !title.isBlank()) {
            String searchKeyword = "%" + title.replace(" ", "") + "%";
            StringTemplate boardTitleNoSpaces = Expressions.stringTemplate(
                    "REPLACE({0}, ' ', '')", board.title);
            condition = condition.and(boardTitleNoSpaces.likeIgnoreCase(searchKeyword));
        }

        long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .where(condition)
                .fetchOne();

        List<BoardResponse> boards = queryFactory
                .selectFrom(board)
                .join(board.users, users).fetchJoin()
                .where(condition)
                .orderBy(board.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch()
                .stream()
                .map(BoardResponse::from)
                .toList();

        int totalPages = (int) Math.ceil((double) totalCount / size);

        return new BoardListResponse(boards, page, size, totalCount, totalPages);
    }
}
