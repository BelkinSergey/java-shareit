package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Marker.OnCreate.class}) @RequestBody UserDto dto) {
        return userClient.createUser(dto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable long id) {
        return userClient.findUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Validated({Marker.OnUpdate.class}) @RequestBody UserDto dto, @PathVariable long id) {
        return userClient.patchUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable long id) {
        userClient.removeUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findUsers();
    }
}