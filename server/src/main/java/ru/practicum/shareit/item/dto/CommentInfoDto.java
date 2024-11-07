package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CommentInfoDto {

    private Long id;

    private String text;

    private String authorName;

    private Long itemId;

    private LocalDateTime created;
}
