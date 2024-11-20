package ru.practicum.shareit.request.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Описание запроса пустое.")
    private String description;
    private LocalDateTime created;
}

