package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.user.UserCreateRequest;
import org.example.bugboard.dto.user.UserResponse;
import org.example.bugboard.dto.user.UserUpdateRequest;
import org.example.bugboard.entity.User;
import org.example.bugboard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        User user = User.builder()
                .provider(request.provider())
                .providerId(request.providerId())
                .email(request.email())
                .name(request.name())
                .nickname(request.nickname())
                .role(request.role())
                .build();
        User saved = userService.create(user);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(UserResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(UserResponse.from(userService.update(id, request.nickname())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
