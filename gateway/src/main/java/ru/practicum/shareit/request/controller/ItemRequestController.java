package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constants.Request;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(Request.USER_ID) Long userId,
                                                @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllWithReplies(@NotNull @RequestHeader(Request.USER_ID) Long userId) {
        return itemRequestClient.findAllWithReplies(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@NotNull @RequestHeader(Request.USER_ID) Long userId) {
        return itemRequestClient.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByIdWithReplies(@RequestHeader(Request.USER_ID) Long userId,
                                                      @PathVariable Long requestId) {
        return itemRequestClient.findByIdWithReplies(userId, requestId);
    }
}