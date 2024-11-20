package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao repository;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(1L, "nick", "nick@mail.ru");
    }

    @Test
    void succeedCreateUser() {
        User userToSave = new User(2L, "nick", "nick@mail.ru");
        when(repository.save(any())).thenReturn(user);

        UserDto userDto = UserMapper.doUserDto(userToSave);
        UserDto userSaved = service.createUser(userDto);

        assertNotNull(userSaved);
        assertEquals(user.getId(), userSaved.getId());
        verify(repository, times(1)).save(any());
    }

    @Test
    void succeedUpdateUserName() {
        long userId = user.getId();
        User userUpdated = new User(1L, "fred", user.getEmail());

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(userUpdated);

        UserDto userDtoUpdated = service.updateUser(new UserDto(1, "fred", null), userId);

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals("fred", userDtoUpdated.getName());
    }

    @Test
    void updateUserBlancName() {
        long userId = user.getId();
        User userUpdated = new User(userId, "nick", user.getEmail());

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(userUpdated);

        UserDto userDtoUpdated = service.updateUser(new UserDto(1, "  ", null), userId);

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals("nick", userDtoUpdated.getName());
    }

    @Test
    void succeedUpdateUserEmail() {
        long userId = user.getId();
        User userUpdated = new User(userId, user.getName(), "fred@mail.ru");

        when(repository.findById(userId)).thenReturn(Optional.of(userUpdated));
        when(repository.save(any())).thenReturn(userUpdated);

        User userToSave = new User(5L, "nick", "nick@example.com");
        UserDto userDto = UserMapper.doUserDto(userToSave);
        service.createUser(userDto);

        UserDto userDtoUpdated = service.updateUser(new UserDto(1, null, "fred@mail.ru"), userId);

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals("fred@mail.ru", userDtoUpdated.getEmail());
    }

    @Test
    void updateUserBlancEmail() {
        long userId = user.getId();
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);
        UserDto userDtoUpdated = service.updateUser(new UserDto(1, null, "  "), userId);

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals(user.getEmail(), userDtoUpdated.getEmail());
    }

    @Test
    void updateNotFoundUser() {
        long userIdNotFound = 0L;
        UserDto userUpdated = new UserDto(1, "nick", "nick@mail.ru");
        when(repository.findById(userIdNotFound)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.updateUser(userUpdated, userIdNotFound));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    void succeedFindUserById() {
        long userId = user.getId();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userFound = service.findUserById(userId);

        assertNotNull(userFound);
        assertEquals(userId, userFound.getId());
    }

    @Test
    void findUserByIdShouldThrowException() {
        long userIdNotFound = 0L;
        when(repository.findById(userIdNotFound)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.findUserById(userIdNotFound));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    void succeedFindAll() {
        when(repository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = service.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void succeedFindAllWithoutUsers() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> users = service.findAll();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void succeedRemoveUser() {
        long userId = 1L;

        service.removeUserById(userId);

        verify(repository, times(1)).deleteById(userId);
    }
}

