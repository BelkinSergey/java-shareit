package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constants.HEADER) long userId, @Validated({Marker.OnCreate.class}) @RequestBody ItemDto dto) {
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constants.HEADER) long userId, @RequestBody ItemDto dto,
                                             @PathVariable long itemId) {
        return itemClient.updateItem(userId, dto, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(Constants.HEADER) Long userId, @PathVariable Long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(Constants.HEADER) long userId) {
        return itemClient.findAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByDescription(@RequestParam(required = false) String text) {
        return itemClient.findItemByDescription(text);
    }

    @DeleteMapping("{itemId}")
    public void removeItemById(@RequestHeader(Constants.HEADER) Long userId,
                               @PathVariable Long itemId) {
        itemClient.removeItemById(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Constants.HEADER) Long userId,
                                             @Validated({Marker.OnCreate.class}) @RequestBody CommentDto commentDto,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(userId, commentDto, itemId);
    }
}