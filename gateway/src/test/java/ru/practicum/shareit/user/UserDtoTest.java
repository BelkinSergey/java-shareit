package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ShareItApp.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Проверяем сериализацию userDto")
    void serializeJsonTest() throws Exception {

        final UserDto userDto = new UserDto();
        userDto.setName("Andrew");
        userDto.setEmail("Komarov@yandex.ru");

        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).contains("\"name\":\"Andrew\"", "\"email\":\"Komarov@yandex.ru\"");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Проверяем дессериализацию")
    void deserializeJsonTest() throws Exception {

        final String json = "{\"id\":1,\"name\":\"Andrew\",\"email\":\"Komarov@yandex.ru\"}";

        final UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("Andrew");
    }

    @Test
    @DisplayName("Проверяем валидацию userDto, правильный пользователь")
    void validationTest() {

        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Andrew");
        userDto.setEmail("Komarov@yandex.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Проверяем валидацию userDto, неправильный пользователь")
    public void invalidTest() {

        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("");
        userDto.setEmail("grom");

        var invalidConstraints = validator.validate(userDto, Marker.OnCreate.class);

        assertThat(invalidConstraints).isNotEmpty();
    }
}
