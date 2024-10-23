package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.group.Marker;


@AllArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст комментария отсутствует.", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String text;
}
