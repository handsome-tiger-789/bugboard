package org.example.bugboard.repository;

import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.dto.board.BoardResponse;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BoardRepositoryImplTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private EntityManager em;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .provider("google")
                .providerId("google-123")
                .email("test@test.com")
                .name("테스트유저")
                .nickname("테스터")
                .role("ROLE_USER")
                .build();
        em.persist(testUser);

        for (int i = 1; i <= 7; i++) {
            Board board = Board.builder()
                    .users(testUser)
                    .title("게시글 제목 " + i)
                    .content("내용 " + i)
                    .build();
            em.persist(board);
        }

        // 삭제된 게시글
        Board deleted = Board.builder()
                .users(testUser)
                .title("삭제된 게시글")
                .content("삭제됨")
                .build();
        em.persist(deleted);
        deleted.softDelete();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("첫 페이지 조회 - 5건 반환, 삭제된 게시글 제외")
    void findBoards_firstPage() {
        BoardListResponse response = boardRepository.findBoards(0, 5, null);

        assertThat(response.boards()).hasSize(5);
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalCount()).isEqualTo(7);
        assertThat(response.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("두 번째 페이지 조회 - 나머지 2건 반환")
    void findBoards_secondPage() {
        BoardListResponse response = boardRepository.findBoards(1, 5, null);

        assertThat(response.boards()).hasSize(2);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.totalCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("created_at DESC 정렬 확인")
    void findBoards_orderedByCreatedAtDesc() {
        BoardListResponse response = boardRepository.findBoards(0, 5, null);

        assertThat(response.boards())
                .extracting(BoardResponse::title)
                .containsExactly(
                        "게시글 제목 7", "게시글 제목 6", "게시글 제목 5",
                        "게시글 제목 4", "게시글 제목 3");
    }

    @Test
    @DisplayName("제목 검색 - 일치하는 게시글만 반환")
    void findBoards_searchByTitle() {
        em.persist(Board.builder().users(testUser).title("인기 게시글").content("내용").build());
        em.persist(Board.builder().users(testUser).title("인기게시글 모음").content("내용").build());
        em.flush();
        em.clear();

        BoardListResponse response = boardRepository.findBoards(0, 5, "인기게시글");

        assertThat(response.boards()).hasSize(2);
        assertThat(response.boards())
                .extracting(BoardResponse::title)
                .allMatch(title -> title.replace(" ", "").contains("인기게시글"));
    }

    @Test
    @DisplayName("제목 검색 - 공백 무시하여 매칭")
    void findBoards_searchIgnoresSpaces() {
        em.persist(Board.builder().users(testUser).title("공 백 테 스 트").content("내용").build());
        em.flush();
        em.clear();

        BoardListResponse response = boardRepository.findBoards(0, 5, "공백테스트");

        assertThat(response.boards()).hasSize(1);
        assertThat(response.boards().getFirst().title()).isEqualTo("공 백 테 스 트");
    }

    @Test
    @DisplayName("검색 결과 없을 때 빈 리스트 반환")
    void findBoards_noResults() {
        BoardListResponse response = boardRepository.findBoards(0, 5, "존재하지않는제목");

        assertThat(response.boards()).isEmpty();
        assertThat(response.totalCount()).isEqualTo(0);
        assertThat(response.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("nickname이 응답에 포함되는지 확인")
    void findBoards_includesNickname() {
        BoardListResponse response = boardRepository.findBoards(0, 5, null);

        assertThat(response.boards())
                .extracting(BoardResponse::nickname)
                .containsOnly("테스터");
    }
}
