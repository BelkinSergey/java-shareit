package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank(message = "Наименование вещи отсутствует.")
    private String name;

    @NotBlank(message = "Описание вещи пустое.")
    private String description;

    @NotNull(message = "Доступность вещи не указана.")
    private Boolean available;

    private Long requestId;
}
