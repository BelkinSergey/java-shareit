package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTest {
    static final String URL = "http://localhost:8080/bookings";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingServiceImpl bookingService;

    BookingDto bookingDto;
    BookingOutputDto bookingOutputDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = new ItemDto(1L, "Table", "black", true, null);

        UserDto userDto = new UserDto(1L, "Nick", "nick@mail.ru");
        bookingDto = new BookingDto(2L, 1L, LocalDateTime.now().plusYears(1), LocalDateTime.now().plusYears(2));
        bookingOutputDto = new BookingOutputDto(2L, itemDto, LocalDateTime.now().plusYears(1), LocalDateTime.now().plusYears(2), userDto, BookingStatus.NEW);
    }

    @Test
    void succeedCreateBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(post(URL)
                        .header(Constants.HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(bookingOutputDto.getId()), Long.class),
                        jsonPath("$.item.id", Matchers.is(bookingOutputDto.getItem().getId()), Long.class),
                        jsonPath("$.booker.id", Matchers.is(bookingOutputDto.getBooker().getId()), Long.class)
                );
    }

    @Test
    void succeedConfirmBookingByOwner() throws Exception {
        bookingOutputDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmBookingByOwner(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutputDto);

        mockMvc.perform(patch(URL + "/2")
                        .header(Constants.HEADER, 1L)
                        .param("approved", String.valueOf(true)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(bookingOutputDto.getId()), Long.class),
                        jsonPath("$.item.id", Matchers.is(bookingOutputDto.getItem().getId()), Long.class),
                        jsonPath("$.status", Matchers.is(bookingOutputDto.getStatus().toString()))
                );

        bookingOutputDto.setStatus(BookingStatus.REJECTED);
        when(bookingService.confirmBookingByOwner(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutputDto);

        mockMvc.perform(patch(URL + "/2")
                        .header(Constants.HEADER, 1L)
                        .param("approved", String.valueOf(false)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(bookingOutputDto.getId()), Long.class),
                        jsonPath("$.item.id", Matchers.is(bookingOutputDto.getItem().getId()), Long.class),
                        jsonPath("$.status", Matchers.is(bookingOutputDto.getStatus().toString()))
                );
    }

    @Test
    void succeedFindBookingById() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(get(URL + "/2")
                        .header(Constants.HEADER, 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(bookingOutputDto.getId()), Long.class),
                        jsonPath("$.item.id", Matchers.is(bookingOutputDto.getItem().getId()), Long.class),
                        jsonPath("$.booker.id", Matchers.is(bookingOutputDto.getBooker().getId()), Long.class)
                );
    }

    @Test
    void succeedFindAllUsersBooking() throws Exception {
        //EmptyList
        when(bookingService.findAllUsersBooking(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL)
                        .header(Constants.HEADER, 1L)
                        .param("state", "rejected")
                )
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("[]")
                );
    }

    @Test
    void succeedFindAllBookingsForItems() throws Exception {
        //EmptyList
        when(bookingService.findAllBookingsForItems(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL + "/owner")
                        .header(Constants.HEADER, 1L)
                        .param("state", "rejected")
                )
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("[]")
                );
    }
}

