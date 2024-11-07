package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentDtoTest {

    ObjectMapper objectMapper;

    Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Проверяем сериализацию объекта CommentDto")
    void serializeJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Hello");

        String jsonContent = objectMapper.writeValueAsString(commentDto);

        assertThat(jsonContent).contains("\"text\":\"Hello\"");
    }

    @Test
    @DisplayName("Проверяем валидацию поля Тест")
    void validationTest() {
        CommentDto validRequest = new CommentDto();
        validRequest.setText("Hello");

        CommentDto invalidRequest = new CommentDto();
        invalidRequest.setText("");

        var validConstraints = validator.validate(validRequest, Marker.OnCreate.class);
        var invalidConstraints = validator.validate(invalidRequest, Marker.OnCreate.class);

        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}