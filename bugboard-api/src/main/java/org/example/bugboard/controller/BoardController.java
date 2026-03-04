package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.board.BoardCreateRequest;
import org.example.bugboard.dto.board.BoardResponse;
import org.example.bugboard.dto.board.BoardUpdateRequest;
import org.example.bugboard.entity.Board;
import org.example.bugboard.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardCreateRequest request) {
        Board saved = boardService.create(request.usersId(), request.title(), request.content());
        return ResponseEntity.created(URI.create("/api/boards/" + saved.getId()))
                .body(BoardResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {
        List<BoardResponse> boards = boardService.findAll().stream()
                .map(BoardResponse::from)
                .toList();
        return ResponseEntity.ok(boards);
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
