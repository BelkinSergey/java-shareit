package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, String state) {
        final Map<String, Object> parameters = Map.of("state", state);
        return get("?state={state}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllBookingsForItems(long userId, State state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()

        );
        return get("/owner?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> confirmBookingByOwner(Long userId, Long bookingId, Boolean approved) {
        return patchWithId("/" + bookingId + "?approved=" + approved, userId);
    }
}

