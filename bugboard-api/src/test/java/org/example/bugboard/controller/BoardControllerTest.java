package org.example.bugboard.controller;

import jakarta.persistence.EntityManager;
import org.example.bugboard.constant.UserHeaders;
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

import org.example.bugboard.entity.Board;

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
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        em.flush();
    }

    // --- Detail ---

    @Test
    @DisplayName("게시글 상세 조회 성공 - 게시글 내용 + 시간 포맷 확인")
    void findById_success() throws Exception {
        Board board = Board.builder()
                .users(testUser)
                .title("상세 조회 테스트")
                .content("상세 내용입니다.")
                .build();
        em.persist(board);
        em.flush();
        em.clear();

        mockMvc.perform(get("/boards/{id}", board.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(board.getId()))
                .andExpect(jsonPath("$.title").value("상세 조회 테스트"))
                .andExpect(jsonPath("$.content").value("상세 내용입니다."))
                .andExpect(jsonPath("$.nickname").value("테스터"))
                .andExpect(jsonPath("$.viewCount").value(1))
                .andExpect(jsonPath("$.createdAt").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")))
                .andExpect(jsonPath("$.updatedAt").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    @DisplayName("게시글 상세 조회 실패 - 존재하지 않는 게시글")
    void findById_failsWhenNotFound() throws Exception {
        mockMvc.perform(get("/boards/{id}", 999L)
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Board not found: 999"));
    }

    // --- Create Success ---

    @Test
    @DisplayName("게시글 작성 성공 - 201 Created, boardId 반환")
    void create_success() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("title", "새 게시글", "content", "게시글 내용"));

        mockMvc.perform(post("/boards")
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").isNumber());
    }

    // --- Create Validation ---

    @Test
    @DisplayName("게시글 작성 실패 - title 누락")
    void create_failsWhenTitleIsBlank() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("title", "", "content", "내용"));

        mockMvc.perform(post("/boards")
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title: 제목이 비어있습니다."));
    }

    @Test
    @DisplayName("게시글 작성 실패 - content 누락")
    void create_failsWhenContentIsBlank() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("title", "제목", "content", ""));

        mockMvc.perform(post("/boards")
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("content: 내용이 비어있습니다."));
    }

    @Test
    @DisplayName("게시글 작성 실패 - 빈 body")
    void create_failsWhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/boards")
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 제목 50자 초과")
    void create_failsWhenTitleExceedsMaxLength() throws Exception {
        String longTitle = "가".repeat(51);
        String body = objectMapper.writeValueAsString(
                Map.of("title", longTitle, "content", "내용"));

        mockMvc.perform(post("/boards")
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title: 제목은 50자 이하로 입력해주세요."));
    }

    // --- Update ---

    @Test
    @DisplayName("게시글 수정 성공 - 본인 게시글 수정")
    void update_success() throws Exception {
        Board board = Board.builder()
                .users(testUser)
                .title("수정 전 제목")
                .content("수정 전 내용")
                .build();
        em.persist(board);
        em.flush();
        em.clear();

        String body = objectMapper.writeValueAsString(
                Map.of("title", "수정 후 제목", "content", "수정 후 내용"));

        mockMvc.perform(put("/boards/{id}", board.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 후 제목"))
                .andExpect(jsonPath("$.content").value("수정 후 내용"));
    }

    @Test
    @DisplayName("게시글 수정 실패 - 다른 사용자의 게시글 수정 불가")
    void update_failsWhenNotOwner() throws Exception {
        Board board = Board.builder()
                .users(testUser)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        em.persist(board);

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
                Map.of("title", "수정 시도", "content", "수정 내용"));

        mockMvc.perform(put("/boards/{id}", board.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(otherUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 게시글만 수정할 수 있습니다."));
    }

    // --- Delete ---

    @Test
    @DisplayName("게시글 삭제 성공 - 본인 게시글 삭제")
    void delete_success() throws Exception {
        Board board = Board.builder()
                .users(testUser)
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .build();
        em.persist(board);
        em.flush();
        em.clear();

        mockMvc.perform(delete("/boards/{id}", board.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(testUser.getId())))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 다른 사용자의 게시글 삭제 불가")
    void delete_failsWhenNotOwner() throws Exception {
        Board board = Board.builder()
                .users(testUser)
                .title("원본 게시글")
                .content("원본 내용")
                .build();
        em.persist(board);

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

        mockMvc.perform(delete("/boards/{id}", board.getId())
                        .header(UserHeaders.USER_ID, String.valueOf(otherUser.getId())))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 게시글만 삭제할 수 있습니다."));
    }
}
