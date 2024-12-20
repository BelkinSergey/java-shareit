package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import ru.practicum.shareit.group.Marker;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto createBooking(@RequestHeader(Constants.HEADER) Long userId,
                                          @Validated({Marker.OnCreate.class}) @RequestBody BookingDto dto) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto confirmBookingByOwner(@RequestHeader(Constants.HEADER) Long userId,
                                                  @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.confirmBookingByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto findBookingById(@RequestHeader(Constants.HEADER) Long userId,
                                            @PathVariable Long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> findAllUsersBooking(@RequestHeader(Constants.HEADER) Long userId,
                                                      @RequestParam String state) {

        return bookingService.findAllUsersBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> findAllBookingsForItems(@RequestHeader(Constants.HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsForItems(userId, state);
    }
}