package ru.practicum.shareit.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.*;

@Repository
@Slf4j
public class UserDaoInMemoryImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    long id = 1;

    @Override
    public User createUser(User user) {

        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Создан пользователь {}", user);
        return user;
    }

    @Override
    public User findUserById(long id) {

        log.info("Найден пользователь с айди {}", id);
        return users.get(id);
    }

    @Override
    public User updateUser(User user, long id) {

        User oldUser = users.get(id);

        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }

        users.remove(id);
        users.put(id, oldUser);
        log.info("Пользователь с айди успешно обновлен {}", id);
        return oldUser;
    }

    @Override
    public void removeUserById(long id) {
        users.remove(id);
        log.info("Пользователь с айди успешно удален {}", id);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Всё пользователь успешно получены");
        return users.values();
    }
}