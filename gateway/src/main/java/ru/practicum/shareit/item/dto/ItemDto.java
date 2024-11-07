package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.group.Marker;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Наименование вещи отсутствует.", groups = {Marker.OnCreate.class})
    private String name;
    @NotBlank(message = "Описание вещи пустое.", groups = {Marker.OnCreate.class})
    private String description;
    @NotNull(message = "Доступность вещи не указана.", groups = {Marker.OnCreate.class})
    private Boolean available;
    private Long requestId;
}