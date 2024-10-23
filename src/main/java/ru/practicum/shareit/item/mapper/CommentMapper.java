package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                user,
                item,
                LocalDateTime.now()
        );
    }

    public static CommentInfoDto toCommentInfoDto(Comment comment) {
        return new CommentInfoDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getItem().getId(),
                comment.getCreated()
        );
    }
}
