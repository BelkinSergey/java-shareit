package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoByOwner;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
public class ItemIntegrationTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;

    private final LocalDateTime now = LocalDateTime.now();

    private User owner;
    private User booker;
    private Item item;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner", "owner@example.com");
        em.persist(owner);

        booker = new User(null, "booker", "booker@example.com");
        em.persist(booker);

        User booker2 = new User(null, "booker2", "booker2@example.com");
        em.persist(booker2);

        item = new Item(null, "table", "black table", true, owner, null);
        em.persist(item);

        item2 = new Item(null, "chair", "black chair", true, owner, null);
        em.persist(item2);

        Booking booking = new Booking(0, item, now.minusDays(5), now.minusDays(4), booker, BookingStatus.APPROVED);
        em.persist(booking);

        Booking booking2 = new Booking(0, item, now.minusDays(3), now.minusDays(2), booker2, BookingStatus.APPROVED);
        em.persist(booking2);

        Comment comment2 = new Comment(null, "table for the whole family", booker2, item, now.minusDays(3));
        em.persist(comment2);
    }

    @Test
    void createItem_Normal() {
        Long userId = owner.getId();
        ItemDto newItem = new ItemDto(null, "bed", "white", true, null);

        ItemDto saved = itemService.createItem(newItem, userId);

        Assertions.assertThat(saved).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newItem);
    }

    @Test
    void updateItem_Normal() {
        ItemDto updater = ItemMapper.doItemDto(item);
        updater.setName("new table");
        updater.setDescription("new black table");
        updater.setAvailable(false);

        ItemDto updated = itemService.updateItem(updater, item.getId(), owner.getId());

        Assertions.assertThat(updated).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updater);
    }

    @Test
    void getAllItems_Normal() {
        Long ownerId = owner.getId();
        Long bookerId = booker.getId();

        List<ItemDtoByOwner> returnedList = itemService.findAll(ownerId);
        Assertions.assertThat(returnedList)
                .isNotEmpty()
                .hasSize(2);
        Assertions.assertThat(returnedList.get(0).getName()).isEqualTo(item.getName());
        Assertions.assertThat(returnedList.get(1).getName()).isEqualTo(item2.getName());


        List<ItemDtoByOwner> returnedList2 = itemService.findAll(bookerId);
        Assertions.assertThat(returnedList2).isEmpty();
    }

    @Test
    void findItems_Normal() {
        String search = "black";

        List<ItemDto> list = itemService.findItemByDescription(search);

        Assertions.assertThat(list).isNotEmpty().hasSize(2);
        Assertions.assertThat(list.get(0).getName()).isEqualTo(item.getName());
        Assertions.assertThat(list.get(1).getName()).isEqualTo(item2.getName());
    }

    @Test
    void findItems_EmptySearchText_Normal() {
        String search = "";

        List<ItemDto> list = itemService.findItemByDescription(search);

        Assertions.assertThat(list).isEmpty();
    }

    @Test
    void addComment_Normal() {
        CommentDto commentNewDto = new CommentDto(null, "perfect table");

        CommentInfoDto addedComment = itemService.addComment(commentNewDto, booker.getId(), item.getId());

        Assertions.assertThat(addedComment).isNotNull()
                .hasFieldOrPropertyWithValue("text", commentNewDto.getText())
                .hasFieldOrPropertyWithValue("authorName", booker.getName());

    }
}

