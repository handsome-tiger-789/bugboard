package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardCreateRequest;
import org.example.bugboard.dto.board.BoardListResponse;
import org.example.bugboard.dto.board.BoardResponse;
import org.example.bugboard.dto.board.BoardUpdateRequest;
import org.example.bugboard.entity.Board;
import org.example.bugboard.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardCreateRequest request) {
        Board saved = boardService.create(request.usersId(), request.title(), request.content());
        return ResponseEntity.created(URI.create("/boards/" + saved.getId()))
                .body(BoardResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.findById(id)));
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
    public ResponseEntity<BoardResponse> update(@PathVariable Long id, @RequestBody BoardUpdateRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.update(id, request.title(), request.content())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
