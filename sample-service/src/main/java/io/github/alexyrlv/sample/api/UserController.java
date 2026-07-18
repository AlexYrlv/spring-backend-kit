package io.github.alexyrlv.sample.api;

import io.github.alexyrlv.sample.api.dto.UserRequest;
import io.github.alexyrlv.sample.api.dto.UserResponse;
import io.github.alexyrlv.sample.service.UserNotFoundException;
import io.github.alexyrlv.sample.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{username}")
    public UserResponse get(@PathVariable String username) {
        return userService.get(username)
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return UserResponse.from(userService.create(request.toDomain()));
    }

    @PutMapping("/{username}")
    public UserResponse update(@PathVariable String username, @Valid @RequestBody UserRequest request) {
        return userService.update(username, request.toDomain())
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String username) {
        if (!userService.delete(username)) {
            throw new UserNotFoundException(username);
        }
    }
}
