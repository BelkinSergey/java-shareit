package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIdIn(Collection<Long> itemIds);

    List<Comment> findAllByItemIdIn(List<Long> itemsId);

    List<Comment> findAllByItemId(Long id);
}




