package org.example.bugboard.controller;

import jakarta.persistence.EntityManager;
import org.example.bugboard.constant.UserHeaders;
import org.example.bugboard.entity.Board;
import org.example.bugboard.entity.Comment;
import org.example.bugboard.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    private Users testUser;
    private Board testBoard;

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

        testBoard = Board.builder()
                .users(testUser)
                .title("테스트 게시글")
                .content("게시글 내용")
                .build();
        em.persist(testBoard);

        em.flush();
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - 댓글 내용 + 시간 포맷 확인")
    void findByBoardId_success() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("첫 번째 댓글")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(comment.getId()))
                .andExpect(jsonPath("$[0].boardId").value(testBoard.getId()))
                .andExpect(jsonPath("$[0].usersId").value(testUser.getId()))
                .andExpect(jsonPath("$[0].nickname").value("테스터"))
                .andExpect(jsonPath("$[0].content").value("첫 번째 댓글"))
                .andExpect(jsonPath("$[0].likeCount").value(0))
                .andExpect(jsonPath("$[0].createdAt").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")))
                .andExpect(jsonPath("$[0].updatedAt").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - 여러 댓글 최신순 정렬")
    void findByBoardId_orderedByCreatedAtDesc() throws Exception {
        Comment comment1 = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("첫 번째 댓글")
                .build();
        em.persist(comment1);
        em.flush();

        Comment comment2 = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("두 번째 댓글")
                .build();
        em.persist(comment2);
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("두 번째 댓글"))
                .andExpect(jsonPath("$[1].content").value("첫 번째 댓글"));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - 삭제된 댓글 제외")
    void findByBoardId_excludesDeletedComments() throws Exception {
        Comment activeComment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("활성 댓글")
                .build();
        em.persist(activeComment);

        Comment deletedComment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("삭제된 댓글")
                .build();
        em.persist(deletedComment);
        em.flush();

        deletedComment.softDelete();
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("활성 댓글"));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - 댓글 없는 게시글은 빈 배열 반환")
    void findByBoardId_returnsEmptyListWhenNoComments() throws Exception {
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - 다른 게시글의 댓글은 포함하지 않음")
    void findByBoardId_excludesOtherBoardComments() throws Exception {
        Board otherBoard = Board.builder()
                .users(testUser)
                .title("다른 게시글")
                .content("다른 내용")
                .build();
        em.persist(otherBoard);

        Comment myBoardComment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("내 게시글 댓글")
                .build();
        em.persist(myBoardComment);

        Comment otherBoardComment = Comment.builder()
                .board(otherBoard)
                .users(testUser)
                .content("다른 게시글 댓글")
                .build();
        em.persist(otherBoardComment);
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("내 게시글 댓글"));
    }
}
