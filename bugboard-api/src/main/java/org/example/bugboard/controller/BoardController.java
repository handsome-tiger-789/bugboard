package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardCreateRequest;
import org.example.bugboard.dto.board.BoardCreateResponse;
import org.example.bugboard.dto.board.BoardDetailResponse;
import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.dto.board.BoardResponse;
import org.example.bugboard.dto.board.BoardUpdateRequest;
import org.example.bugboard.security.HeaderUserInfo;
import org.example.bugboard.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardCreateResponse> create(
            @AuthenticationPrincipal HeaderUserInfo userInfo,
            @Valid @RequestBody BoardCreateRequest request) {
        Long boardId = boardService.create(userInfo.userId(), request);
        return ResponseEntity.created(URI.create("/boards/" + boardId))
                .body(new BoardCreateResponse(boardId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardDetailResponse.from(boardService.findById(id)));
    }

    /**
     * Board List API
     * @return {@code ResponseEntity<BoardListResponse>}
     * */
    @GetMapping
    public ResponseEntity<BoardListResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(boardService.findAll(page, title));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal HeaderUserInfo userInfo,
            @Valid @RequestBody BoardUpdateRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.update(id, userInfo.userId(), request.title(), request.content())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal HeaderUserInfo userInfo) {
        boardService.delete(id, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
