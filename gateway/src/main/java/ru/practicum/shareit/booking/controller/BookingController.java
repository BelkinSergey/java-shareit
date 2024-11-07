package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.group.Marker;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@NotNull @RequestHeader(Constants.HEADER) Long userId,
                                                @Validated({Marker.OnCreate.class}) @RequestBody BookingDto requestDto) {
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBookingByOwner(@RequestHeader(Constants.HEADER) Long userId,
                                                        @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingClient.confirmBookingByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Constants.HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsersBooking(@RequestHeader(Constants.HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsForItems(@RequestHeader(Constants.HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL") @NotBlank String state) {
        return bookingClient.findAllBookingsForItems(userId, State.valueOf(state));

    }
}