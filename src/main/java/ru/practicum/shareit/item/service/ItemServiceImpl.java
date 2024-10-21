package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidParameterException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoByOwner;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemDao dao;
    private final CommentDao commentDao;
    private final BookingDao bookingDao;
    private final UserDao userDao;

    @Override
    public ItemDto createItem(ItemDto dto, long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("Нет юзера по id: " + userId));
        Item item = ItemMapper.toItem(dto, user);

        return ItemMapper.doItemDto(dao.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        userDao.findById(userId).orElseThrow(() -> new NotFoundException("Нет юзера по id: " + userId));
        dao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id: " + userId));
        ;
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        checkAccess(userId, itemId);

        return ItemMapper.doItemDto(dao.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoByOwner findItemById(long userId, long itemId) {
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена."));
        List<Comment> comments = commentDao.findByItemId(itemId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsBeforeAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);
        List<Booking> nextBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsAfterAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);

        log.info("Найдена вещь с айди {}", itemId);
        return ItemMapper.doItemDtoByOwner(item, lastBookings, nextBookings, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoByOwner> findAll(long userId) {
        List<Item> userItems = dao.findItemsByOwnerId(userId);
        List<Comment> comments = commentDao.findByItemIdIn(userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        LocalDateTime now = LocalDateTime.now();

        log.info("Найден список вещей пользователя с айди {}", userId);
        return userItems.stream()
                .map(item -> ItemMapper.doItemDtoByOwner(item,
                        bookingDao.findByItemIdAndItemOwnerIdAndStartIsBeforeAndStatusIsNot(item.getId(), userId, now,
                                BookingStatus.REJECTED),
                        bookingDao.findByItemIdAndItemOwnerIdAndStartIsAfterAndStatusIsNot(item.getId(), userId, now,
                                BookingStatus.REJECTED),
                        comments))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findItemByDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Найден список вещей по текстовому запросу {}", text);
        return dao.findByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text, text)
                .stream()
                .map(ItemMapper::doItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeItemById(long userId, long itemId) {
        checkAccess(userId, itemId);
        dao.deleteById(itemId);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя по id: " + userId));
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета по id: " + itemId));

        if (bookingDao.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new NotValidParameterException("Ошибка написания отзыва. Пользователь не бронировал вещь.");
        }

        final Comment comment = CommentMapper.toComment(commentDto, user, item);
        commentDao.save(comment);

        log.info("Пользователь с id {} оставил комментарий к вещи с id {}.", userId, itemId);
        return CommentMapper.toCommentDto(comment);
    }

    private void checkAccess(long userId, long itemId) {
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));
        Long ownerId = item.getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Редактирование вещи доступно только владельцу.");
        }
    }
}


