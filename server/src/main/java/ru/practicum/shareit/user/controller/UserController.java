package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto createUser(@Validated({Marker.OnCreate.class}) @RequestBody UserDto dto) {
        return userService.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable long id) {
        return userService.findUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated({Marker.OnUpdate.class}) @RequestBody UserDto dto, @PathVariable long id) {
        return userService.updateUser(dto, id);
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable long id) {
        userService.removeUserById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }
}