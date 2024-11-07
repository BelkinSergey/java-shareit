package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(Long ownerId);


    @Query("""
            SELECT i
            FROM Item i
            WHERE i.available = true
                AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%'))
                OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?2, '%')))
            """)
    List<Item> findByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String text, String texts);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}