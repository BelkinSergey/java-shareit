package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
public class Item {
    private Long id;


    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
