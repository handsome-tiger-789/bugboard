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


}
