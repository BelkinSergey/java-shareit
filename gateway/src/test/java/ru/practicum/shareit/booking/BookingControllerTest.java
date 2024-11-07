package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
public class BookingControllerTest {

    private static final String URL = "http://localhost:8080/bookings";


    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("Проверяем сериализацию Json")
    void serializeJsonTest() throws Exception {

        final BookingDto bookDto = new BookingDto();
        bookDto.setItemId(1L);
        bookDto.setStart(LocalDateTime.now().plusDays(1));
        bookDto.setEnd(LocalDateTime.now().plusDays(3));

        String json = objectMapper.writeValueAsString(bookDto);
        assertThat(json).contains("\"itemId\":1");
    }

    @Test
    @DisplayName("Проверяем Дессериализацию")
    void deserializeJsonTest() throws Exception {

        String json = "{\"itemId\":1,\"start\":\"2024-09-28T10:00:00\",\"end\":\"2024-09-29T10:00:00\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);
        assertThat(bookingDto.getItemId()).isEqualTo(1);
    }

}