package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.comment.CommentCreateRequest;
import org.example.bugboard.dto.comment.CommentResponse;
import org.example.bugboard.dto.comment.CommentUpdateRequest;
import org.example.bugboard.entity.Comment;
import org.example.bugboard.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> create(@PathVariable Long boardId, @RequestBody CommentCreateRequest request) {
        Comment saved = commentService.create(boardId, request.usersId(), request.content());
        return ResponseEntity.created(URI.create("/api/comments/" + saved.getId()))
                .body(CommentResponse.from(saved));
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> findByBoardId(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.findByBoardId(boardId).stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id, @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(CommentResponse.from(commentService.update(id, request.content())));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
