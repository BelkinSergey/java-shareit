package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
public class Item {
    private Long id;

    @NotBlank(message = "Наименование вещи отсутствует.")
    private String name;

    @NotBlank(message = "Описание вещи пустое.")
    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
