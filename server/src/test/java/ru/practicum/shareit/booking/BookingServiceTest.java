package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidParameterException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingDao bookingDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ItemDao itemDao;

    @InjectMocks
    private BookingServiceImpl service;

    private static final LocalDateTime NOW = LocalDateTime.now();
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingToSave;

    @BeforeEach
    void started() {
        owner = new User(1L, "nick", "nick@mail.ru");

        booker = new User(2L, "fred", "fred@mail.ru");

        item = new Item(4L, "table", "red", true, owner, null);

        booking = new Booking(1L, item, NOW.plusDays(20), NOW.plusDays(30), booker, BookingStatus.WAITING);

        bookingToSave = new BookingDto(1L, item.getId(), booking.getStart(), booking.getEnd());
    }

    @Test
    void succeedCreateBooking() {
        when(userDao.existsById(anyLong())).thenReturn(true);
        when(itemDao.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingDao.save(any())).thenReturn(booking);

        BookingOutputDto bookingOutDto = service.createBooking(bookingToSave, booker.getId());

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void createBookingFailByValidationPeriod() {
        bookingToSave.setStart(booking.getEnd());
        bookingToSave.setEnd(booking.getStart());

        NotValidParameterException exception = assertThrows(
                NotValidParameterException.class,
                () -> service.createBooking(bookingToSave, booker.getId()));

        assertEquals("Дата окончания бронирования раньше даты начала или равней ей.", exception.getMessage());
    }

    @Test
    void createBookingFailByUserNotFound() {
        long userNotFoundId = 0L;
        String error = "Пользователь с запрашиваемым айди не зарегистрирован.";
        when(userDao.existsById(anyLong())).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createBooking(bookingToSave, userNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(bookingDao, times(0)).save(any());
    }

    @Test
    void createBookingFailByItemNotFound() {
        long itemNotFoundId = 0L;
        bookingToSave.setItemId(itemNotFoundId);

        when(userDao.existsById(anyLong())).thenReturn(true);
        when(itemDao.findById(itemNotFoundId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createBooking(bookingToSave, booker.getId()));

        assertEquals("Вещь с указанным айди не найдена.", exception.getMessage());
    }

    @Test
    void createBookingFailByItemNotAvailable() {
        item.setAvailable(false);
        when(userDao.existsById(anyLong())).thenReturn(true);
        when(itemDao.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        NotValidParameterException exception = assertThrows(
                NotValidParameterException.class,
                () -> service.createBooking(bookingToSave, booker.getId()));

        assertEquals("Вещь уже забронирована.", exception.getMessage());

        item.setAvailable(true);
        item.setOwner(booker);
        when(userDao.existsById(anyLong())).thenReturn(true);
        when(itemDao.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.createBooking(bookingToSave, booker.getId()));

        assertEquals("Владелец вещи не может её забронировать.", e.getMessage());
    }

    @Test
    void succeedConfirmBookingByOwner() {
        when(bookingDao.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingDao.save(any())).thenReturn(booking);

        BookingOutputDto bookingOutDto = service.confirmBookingByOwner(owner.getId(), booking.getId(), true);

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingOutDto.getStatus());

        bookingOutDto = service.confirmBookingByOwner(owner.getId(), booking.getId(), false);

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
        assertEquals(BookingStatus.REJECTED, bookingOutDto.getStatus());
    }

    @Test
    void confirmBookingByOwnerFailByBookingNotFound() {

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.confirmBookingByOwner(owner.getId(), booking.getId(), true)
        );

        assertEquals("Бронирование с указанным айди не найдено.", exception.getMessage());
        verify(bookingDao, times(0)).save(any());
    }

    @Test
    void confirmBookingByOwnerFailByNotValidParameter() {

        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        NotValidParameterException exception = assertThrows(
                NotValidParameterException.class,
                () -> service.confirmBookingByOwner(owner.getId(), booking.getId(), false)
        );

        assertEquals("Бронирование уже отклонено.", exception.getMessage());
        verify(bookingDao, times(0)).save(any());

        booking.setStatus(BookingStatus.APPROVED);
        NotValidParameterException ex = assertThrows(
                NotValidParameterException.class,
                () -> service.confirmBookingByOwner(owner.getId(), booking.getId(), true)
        );

        assertEquals("Бронирование уже подтверждено.", ex.getMessage());
        verify(bookingDao, times(0)).save(any());
    }

    @Test
    void succeedFindBookingById() {
        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingOutputDto bookingOutDto = service.findBookingById(owner.getId(), booker.getId());

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void findBookingByIdFailByBookingNotFound() {
        when(bookingDao.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.findBookingById(owner.getId(), booker.getId())
        );

        assertEquals("Бронирование с указанным айди не найдено.", exception.getMessage());
    }

    @Test
    void findBookingByIdFailByItemNotAvailable() {
        when(bookingDao.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        NotAccessException exception = assertThrows(
                NotAccessException.class,
                () -> service.findBookingById(0L, booker.getId())
        );

        assertEquals("Получение данных доступно либо автору бронирования, либо владельцу вещи",
                exception.getMessage());
    }

    @Test
    void findAllBookingsForItemsFailByUserWithoutItems() {
        String error = "У пользователя нет вещей.";
        when(userDao.existsById(anyLong())).thenReturn(true);
        when(itemDao.findItemsByOwnerId(anyLong())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.findAllBookingsForItems(owner.getId(), "all")
        );

        assertEquals(error, exception.getMessage());
    }
}

