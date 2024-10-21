package ru.practicum.shareit.exception;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class AlreadyExistException extends RuntimeException {
    private static final String MESSAGE = "%s уже зарегистрирован.";

    public AlreadyExistException(User user) {
        super(String.format(MESSAGE, user));
    }

    public AlreadyExistException(Item item) {
        super(String.format(MESSAGE, item));
    }
}

