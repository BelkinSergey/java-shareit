package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoByOwner;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(Constants.HEADER) Long userId,
                                        @RequestBody ItemRequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoByOwner> findAllWithReplies(@RequestHeader(Constants.HEADER) Long userId) {
        return requestService.findAllUsersRequestsWithReplies(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoByOwner> findAll(@RequestHeader(Constants.HEADER) Long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoByOwner findByIdWithReplies(@RequestHeader(Constants.HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        return requestService.findByIdWithReplies(userId, requestId);
    }
}

