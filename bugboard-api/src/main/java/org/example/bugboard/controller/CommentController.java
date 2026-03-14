package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.comment.CommentCreateRequest;
import org.example.bugboard.dto.comment.CommentCreateResponse;
import org.example.bugboard.dto.comment.CommentResponse;
import org.example.bugboard.dto.comment.CommentUpdateRequest;
import org.example.bugboard.security.HeaderUserInfo;
import org.example.bugboard.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentCreateResponse> create(
            @PathVariable Long boardId,
            @AuthenticationPrincipal HeaderUserInfo userInfo,
            @Valid @RequestBody CommentCreateRequest request) {
        Long commentId = commentService.create(boardId, userInfo.userId(), request.content());
        return ResponseEntity.created(URI.create("/comments/" + commentId))
                .body(new CommentCreateResponse(commentId));
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> findByBoardId(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.findByBoardId(boardId).stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal HeaderUserInfo userInfo,
            @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(CommentResponse.from(commentService.update(id, userInfo.userId(), request.content())));
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Void> like(@PathVariable Long id) {
        commentService.like(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal HeaderUserInfo userInfo) {
        commentService.delete(id, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
