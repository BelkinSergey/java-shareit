package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoByOwner;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    public static final String HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER) long userId, @Validated({Marker.OnCreate.class}) @RequestBody ItemDto dto) {
        return service.createItem(dto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto dto,
                              @PathVariable long itemId) {
        return service.updateItem(dto, itemId, userId);
    }

    @GetMapping("{itemId}")
    public ItemDtoByOwner findItemById(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId) {
        return service.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoByOwner> findAll(@RequestHeader(HEADER) long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByDescription(@RequestParam String text) {
        return service.findItemByDescription(text);
    }

    @DeleteMapping("{itemId}")
    public void removeItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        service.removeItemById(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public CommentInfoDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Validated({Marker.OnCreate.class, Marker.OnUpdate.class}) @RequestBody CommentDto commentDto,
                                     @PathVariable Long itemId) {
        return service.addComment(commentDto, userId, itemId);
    }
}