package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidParameterException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao dao;
    private final UserService userService;

    public ItemDto createItem(ItemDto dto, long userId) {
        userService.findUserById(userId);
        Item item = ItemMapper.toItem(dto);

        return ItemMapper.doItemDto(dao.createItem(item, userId));
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {

        if (dto.getName() != null && dto.getName().isBlank()) {
            throw new NotValidParameterException("Название не может быть пустым");
        }
        if (dto.getDescription() != null && dto.getDescription().isBlank()) {
            throw new NotValidParameterException("Описание не может быть пустым");
        }
        checkItemAvailability(itemId);
        checkAccess(userId, itemId);
        Item item = ItemMapper.toItem(dto);

        return ItemMapper.doItemDto(dao.updateItem(item, itemId, userId));
    }

    public ItemDto findItemById(long itemId) {
        checkItemAvailability(itemId);
        return ItemMapper.doItemDto(dao.findItemById(itemId));
    }

    public List<ItemDto> findAll(long userId) {
        return dao.findAll(userId).stream()
                .map(ItemMapper::doItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> findItemByDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return dao.findItemByDescription(text.toLowerCase()).stream()
                .map(ItemMapper::doItemDto)
                .collect(Collectors.toList());
    }

    public void removeItemById(long userId, long itemId) {
        checkAccess(userId, itemId);
        dao.removeItemById(userId, itemId);
    }

    private void checkAccess(long userId, long itemId) {
        Item item = dao.findItemById(itemId);
        Long ownerId = item.getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Редактирование вещи доступно только владельцу.");
        }
    }

    private void checkItemAvailability(long itemId) {
        if (dao.findItemById(itemId) == null) {
            throw new NotFoundException("Вещь с указанным айди не найдена.");
        }
    }
}

