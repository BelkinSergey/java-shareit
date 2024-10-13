package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Описание запроса пустое.")
    private String description;

    @NotNull(message = "Пользователь запроса не указан.")
    private User requester;

    @NotNull(message = "Дата создания не указана.")
    private LocalDateTime created;
}


