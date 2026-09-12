package io.gh.helder.aisafe.identity.web;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gh.helder.aisafe.identity.service.UserService;
import io.gh.helder.aisafe.identity.web.dto.CreateUserRequest;
import io.gh.helder.aisafe.identity.web.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserService users;

    UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var user = users.register(request);
        return ResponseEntity
                .created(URI.create("/api/users/" + user.id()))
                .body(UserResponse.from(user));
    }

    @GetMapping
    Page<UserResponse> list(@PageableDefault(size = 20, sort = "email.value") Pageable pageable) {
        return users.findAll(pageable).map(UserResponse::from);
    }

    @GetMapping("/{id}")
    UserResponse byId(@PathVariable UUID id) {
        return UserResponse.from(users.byId(id));
    }

    @PostMapping("/{id}/disable")
    UserResponse disable(@PathVariable UUID id) {
        return UserResponse.from(users.disable(id));
    }

    @PostMapping("/{id}/enable")
    UserResponse enable(@PathVariable UUID id) {
        return UserResponse.from(users.enable(id));
    }
}
