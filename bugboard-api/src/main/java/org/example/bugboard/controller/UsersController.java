package org.example.bugboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.dto.users.UsersCreateRequest;
import org.example.bugboard.dto.users.UsersResponse;
import org.example.bugboard.dto.users.UsersUpdateRequest;
import org.example.bugboard.entity.Users;
import org.example.bugboard.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    public ResponseEntity<UsersResponse> create(@RequestBody UsersCreateRequest request) {
        Users users = Users.builder()
                .provider(request.provider())
                .providerId(request.providerId())
                .email(request.email())
                .name(request.name())
                .nickname(request.nickname())
                .role(request.role())
                .build();
        Users saved = usersService.create(users);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(UsersResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UsersResponse.from(usersService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UsersResponse>> findAll() {
        List<UsersResponse> users = usersService.findAll().stream()
                .map(UsersResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersResponse> update(@PathVariable Long id, @RequestBody UsersUpdateRequest request) {
        return ResponseEntity.ok(UsersResponse.from(usersService.update(id, request.nickname())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usersService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
