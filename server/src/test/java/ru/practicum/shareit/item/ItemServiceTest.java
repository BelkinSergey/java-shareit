package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    @Mock
    private ItemDao repository;

    @Mock
    private UserDao userRepository;

    @Mock
    private BookingDao bookingRepository;

    @Mock
    private CommentDao commentRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private User owner;
    private User booker;
    private Item item;
    private Item item2;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setup() {
        LocalDateTime start = NOW.minusDays(3);
        LocalDateTime end = NOW.minusDays(1);

        owner = new User(1L, "nick", "nick@mail.ru");

        booker = new User(2L, "fred", "fred@mail.ru");

        item = new Item(4L, "table", "red", true, owner, null);

        item2 = new Item(5L, "bed", "white", true, owner, null);

        booking = new Booking(1L, item, start, end, booker, BookingStatus.APPROVED);

        comment = new Comment(1L, "super", booker, item, NOW);
    }

    @Test
    void succeedCreateItem() {
        when(repository.save(any())).thenReturn(item);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        ItemDto itemDto = service.createItem(ItemMapper.doItemDto(item), owner.getId());

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        verify(repository, times(1)).save(any());
    }

    @Test
    void createItemFailByUserNotFound() {
        long userNotFoundId = 0L;
        String error = "Пользователь не найден.";
        when(userRepository.findById(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createItem(ItemMapper.doItemDto(item), userNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(repository, times(0)).save(any());
    }

    @Test
    void succeedUpdateItem() {
        long itemId = item.getId();
        long userId = owner.getId();
        Item updatedItem = new Item(itemId, "chair", "black", false, null, null);

        when(repository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(repository.save(any())).thenReturn(updatedItem);

        ItemDto itemDtoUpdated = service.updateItem(ItemMapper.doItemDto(updatedItem), itemId, userId);

        assertNotNull(itemDtoUpdated);
        assertEquals(itemId, itemDtoUpdated.getId());
        assertEquals("chair", itemDtoUpdated.getName());
        assertEquals("black", itemDtoUpdated.getDescription());
        assertEquals(false, itemDtoUpdated.getAvailable());
    }

    @Test
    void updateItemFailByUserNotFound() {
        User user = new User(0L, "nick", "nick@mail.ru");
        long itemId = item.getId();
        long userId = user.getId();

        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.updateItem(ItemMapper.doItemDto(item), itemId, userId));

        assertEquals("Нет юзера по id: " + userId, exception.getMessage());
    }

    @Test
    void updateItemFailByItemNotFound() {
        long userId = owner.getId();
        long itemNotFoundId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(repository.findById(itemNotFoundId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.updateItem(ItemMapper.doItemDto(item), itemNotFoundId, userId));

        assertEquals("Вещи нет", exception.getMessage());
    }

    @Test
    void updateItemBlancName() {
        long itemId = item.getId();
        long userId = owner.getId();
        Item updatedItem = new Item(itemId, null, "black", false, null, null);

        when(repository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(repository.save(any())).thenReturn(item);

        ItemDto itemDtoUpdated = service.updateItem(ItemMapper.doItemDto(updatedItem), itemId, userId);

        assertNotNull(itemDtoUpdated);
        assertEquals(itemId, itemDtoUpdated.getId());
        assertEquals("table", itemDtoUpdated.getName());
    }

    @Test
    void updateItemBlancDescription() {
        long itemId = item.getId();
        long userId = owner.getId();
        Item updatedItem = new Item(itemId, "table", null, false, null, null);

        when(repository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(repository.save(any())).thenReturn(item);

        ItemDto itemDtoUpdated = service.updateItem(ItemMapper.doItemDto(updatedItem), itemId, userId);

        assertNotNull(itemDtoUpdated);
        assertEquals(itemId, itemDtoUpdated.getId());
        assertEquals("red", itemDtoUpdated.getDescription());
    }

    @Test
    void updateItemBlancAvailable() {
        long itemId = item.getId();
        long userId = owner.getId();
        Item updatedItem = new Item(itemId, "table", "black", null, null, null);

        when(repository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(repository.save(any())).thenReturn(item);

        ItemDto itemDtoUpdated = service.updateItem(ItemMapper.doItemDto(updatedItem), itemId, userId);

        assertNotNull(itemDtoUpdated);
        assertEquals(itemId, itemDtoUpdated.getId());
        assertEquals(true, itemDtoUpdated.getAvailable());
    }

    @Test
    void findItemByIdFailItemNotFound() {
        long itemNotFoundId = 0L;
        String error = "Вещь не найдена.";
        when(repository.findById(itemNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.findItemById(owner.getId(), itemNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void succeedRemoveItemById() {
        long userId = owner.getId();
        long itemId = item.getId();

        when(repository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(repository).deleteById(itemId);

        service.removeItemById(userId, itemId);

        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void removeItemByIdFailItemNotFound() {
        long itemNotFoundId = 0L;
        String error = "Вещь не найдена.";
        when(repository.findById(itemNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.removeItemById(owner.getId(), itemNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void removeItemByIdFailAccess() {
        long itemId = item.getId();
        long notOwnerId = booker.getId();
        String error = "Редактирование вещи доступно только владельцу.";
        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.removeItemById(notOwnerId, itemId));

        assertEquals(error, exception.getMessage());
    }
}
