package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookingDto dto, Item item, User booker) {
        return new Booking(
                dto.getId(),
                item,
                dto.getStart(),
                dto.getEnd(),
                booker,
                BookingStatus.WAITING
        );
    }

    public static BookingDto doBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd()

        );
    }

    public static BookingOutputDto doBookingOutputDto(Booking booking) {
        ItemDto itemDto = ItemMapper.doItemDto(booking.getItem());
        UserDto userDto = UserMapper.doUserDto(booking.getBooker());
        return new BookingOutputDto(
                booking.getId(),
                itemDto,
                booking.getStart(),
                booking.getEnd(),
                userDto,
                booking.getStatus()
        );
    }

    public static List<BookingOutputDto> makeBookingsOutputList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::doBookingOutputDto).collect(Collectors.toList());
    }
}
