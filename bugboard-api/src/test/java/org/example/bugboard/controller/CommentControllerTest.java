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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private ObjectMapper objectMapper;

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

    // --- Create ---

    @Test
    @DisplayName("댓글 작성 성공 - 201 Created, commentId 반환")
    void create_success() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("content", "새 댓글입니다."));

        mockMvc.perform(post("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").isNumber());
    }

    @Test
    @DisplayName("댓글 작성 실패 - content 누락")
    void create_failsWhenContentIsBlank() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("content", ""));

        mockMvc.perform(post("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("content: 내용이 비어있습니다."));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 빈 body")
    void create_failsWhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 게시글")
    void create_failsWhenBoardNotFound() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("content", "댓글 내용"));

        mockMvc.perform(post("/boards/{boardId}/comments", 999L)
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Board not found: 999"));
    }

    // --- Update ---

    @Test
    @DisplayName("댓글 수정 성공 - 본인 댓글 수정")
    void update_success() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("수정 전 댓글")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        String body = objectMapper.writeValueAsString(
                Map.of("content", "수정 후 댓글"));

        mockMvc.perform(put("/comments/{id}", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.content").value("수정 후 댓글"));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 다른 사용자의 댓글 수정 불가")
    void update_failsWhenNotOwner() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("원본 댓글")
                .build();
        em.persist(comment);

        Users otherUser = Users.builder()
                .provider("google")
                .providerId("google-456")
                .email("other@test.com")
                .name("다른유저")
                .nickname("다른사람")
                .role("ROLE_USER")
                .build();
        em.persist(otherUser);
        em.flush();
        em.clear();

        String body = objectMapper.writeValueAsString(
                Map.of("content", "수정 시도"));

        mockMvc.perform(put("/comments/{id}", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(otherUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 댓글만 수정할 수 있습니다."));
    }

    @Test
    @DisplayName("댓글 수정 실패 - content 누락")
    void update_failsWhenContentIsBlank() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("원본 댓글")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        String body = objectMapper.writeValueAsString(
                Map.of("content", ""));

        mockMvc.perform(put("/comments/{id}", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("content: 내용이 비어있습니다."));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void update_failsWhenCommentNotFound() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("content", "수정 내용"));

        mockMvc.perform(put("/comments/{id}", 999L)
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found: 999"));
    }

    // --- Delete ---

    @Test
    @DisplayName("댓글 삭제 성공 - 본인 댓글 삭제")
    void delete_success() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("삭제할 댓글")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        mockMvc.perform(delete("/comments/{id}", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 다른 사용자의 댓글 삭제 불가")
    void delete_failsWhenNotOwner() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("원본 댓글")
                .build();
        em.persist(comment);

        Users otherUser = Users.builder()
                .provider("google")
                .providerId("google-456")
                .email("other@test.com")
                .name("다른유저")
                .nickname("다른사람")
                .role("ROLE_USER")
                .build();
        em.persist(otherUser);
        em.flush();
        em.clear();

        mockMvc.perform(delete("/comments/{id}", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(otherUser.getId())))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 댓글만 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    void delete_failsWhenCommentNotFound() throws Exception {
        mockMvc.perform(delete("/comments/{id}", 999L)
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found: 999"));
    }

    // --- Like ---

    @Test
    @DisplayName("댓글 좋아요 성공 - likeCount 1 증가")
    void like_success() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("좋아요 테스트 댓글")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        mockMvc.perform(post("/comments/{id}/like", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        // 댓글 조회하여 likeCount 확인
        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likeCount").value(1));
    }

    @Test
    @DisplayName("댓글 좋아요 여러 번 - likeCount 누적")
    void like_multiple() throws Exception {
        Comment comment = Comment.builder()
                .board(testBoard)
                .users(testUser)
                .content("좋아요 누적 테스트")
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        mockMvc.perform(post("/comments/{id}/like", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/comments/{id}/like", comment.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/boards/{boardId}/comments", testBoard.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likeCount").value(2));
    }

    @Test
    @DisplayName("댓글 좋아요 실패 - 존재하지 않는 댓글")
    void like_failsWhenCommentNotFound() throws Exception {
        mockMvc.perform(post("/comments/{id}/like", 999L)
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found: 999"));
    }

    // --- List ---

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
