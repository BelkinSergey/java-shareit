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
import ru.practicum.shareit.item.dto.CommentInfoDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        if (dto.getName() != null && !(dto.getName().isBlank())) {
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
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id:" + itemId));
        Long ownerId = item.getOwner().getId();
        List<Comment> comments = commentDao.findAllByItemId(item.getId());
        List<Booking> bookings = bookingDao.findAllByItemIdAndEndBefore(itemId, LocalDateTime.now());
        Booking lastBooking = getLastBooking(bookings);
        Booking nextBooking = getNextBooking(bookings);
        return ItemMapper.doItemDtoByOwner(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoByOwner> findAll(long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("Нет пользователя по id {} " + userId));
        List<Item> items = dao.findAllByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).toList();
        List<Booking> bookings = bookingDao.findAllByIdInAndEndBefore(itemsId, LocalDateTime.now());
        List<Comment> comments = commentDao.findAllByItemIdIn(itemsId);
        Map<Long, List<Booking>> bookingsMapByItemsId = new HashMap<>();
        Map<Long, List<Comment>> commentsMapByItemsID = new HashMap<>();
        for (Booking booking : bookings) {
            bookingsMapByItemsId.computeIfAbsent(booking.getItem().getId(), k -> new ArrayList<>()).add(booking);
        }
        for (Comment comment : comments) {
            commentsMapByItemsID.computeIfAbsent(comment.getItem().getId(), c -> new ArrayList<>()).add(comment);
        }
        List<ItemDtoByOwner> itemInfoDto = new ArrayList<>();

        for (Item item : items) {
            List<Comment> listComments = commentsMapByItemsID.getOrDefault(item.getId(), new ArrayList<>());
            List<Booking> itemBookings = bookingsMapByItemsId.getOrDefault(item.getId(), new ArrayList<>());
            Booking lastBooking = getLastBooking(itemBookings);
            Booking nextBooking = getNextBooking(itemBookings);

            ItemDtoByOwner infoDto = ItemMapper.doItemDtoByOwner(item, lastBooking, nextBooking, listComments);
            itemInfoDto.add(infoDto);
        }
        return itemInfoDto;

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
    public CommentInfoDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя по id: " + userId));
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета по id: " + itemId));

        if (bookingDao.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new NotValidParameterException("Ошибка написания отзыва. Пользователь не бронировал вещь.");
        }

        final Comment comment = CommentMapper.toComment(commentDto, user, item);
        commentDao.save(comment);

        log.info("Пользователь с id {} оставил комментарий к вещи с id {}.", userId, itemId);
        return CommentMapper.toCommentInfoDto(comment);
    }

    private void checkAccess(long userId, long itemId) {
        Item item = dao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));
        Long ownerId = item.getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Редактирование вещи доступно только владельцу.");
        }
    }

    private Booking getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart() != null)
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getEnd() != null)
                .max(Comparator.comparing(Booking::getEnd));
        return lastBooking.orElse(null);
    }
}