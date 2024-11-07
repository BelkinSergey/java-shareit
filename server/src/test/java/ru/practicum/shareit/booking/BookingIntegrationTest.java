package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {
    @Autowired
    private final EntityManager em;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final BookingDao bookingDao;

    private final LocalDateTime now = LocalDateTime.now();

    private User owner;
    private User booker;
    private Item item1;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner", "owner@example.com");
        em.persist(owner);

        booker = new User(null, "booker", "booker@example.com");
        em.persist(booker);

        item1 = new Item(null, "table", "black", true, owner, null);
        em.persist(item1);

        Item item2 = new Item(null, "chair", "white", true, owner, null);
        em.persist(item2);

        booking1 = new Booking(0, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), booker, BookingStatus.APPROVED);
        em.persist(booking1);

        booking2 = new Booking(0, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), booker, BookingStatus.WAITING);
        em.persist(booking2);

        booking3 = new Booking(0, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), booker, BookingStatus.REJECTED);
        em.persist(booking3);

        booking4 = new Booking(0, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), booker, BookingStatus.CANCELED);
        em.persist(booking4);

    }

    @Test
    void createBooking_Normal() {
        Long userId = booker.getId();
        Long itemId = item1.getId();

        BookingDto newBooking = new BookingDto(10L, itemId, now.plusDays(2), now.plusDays(4));

        BookingOutputDto created = bookingService.createBooking(newBooking, userId);

        Booking retrievedBooking = bookingDao.findById(created.getId()).orElse(null);
        Assertions.assertThat(retrievedBooking).isNotNull();
        Assertions.assertThat(retrievedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        Assertions.assertThat(retrievedBooking.getBooker().getId()).isEqualTo(userId);
        Assertions.assertThat(retrievedBooking.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void approve_Normal() {
        Long userId = owner.getId();
        Long bookingId = booking2.getId();

        Assertions.assertThat(bookingService.findBookingById(booker.getId(), bookingId))
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);

        BookingOutputDto approvedBooking = bookingService.confirmBookingByOwner(userId, bookingId, true);

        Assertions.assertThat(approvedBooking).isNotNull()
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
    }

    @Test
    void getBooking_Normal() {
        Long userId = booker.getId();
        Long bookingId = booking3.getId();

        BookingOutputDto finder = bookingService.findBookingById(userId, bookingId);

        Assertions.assertThat(finder).isNotNull()
                .hasFieldOrPropertyWithValue("id", bookingId);
        Assertions.assertThat(finder.getBooker())
                .hasFieldOrPropertyWithValue("id", userId);
    }

    @Test
    void getAllBookings_All() {
        List<BookingOutputDto> list1 = bookingService.findAllUsersBooking(booker.getId(), "All");
        Assertions.assertThat(list1).isNotEmpty().hasSize(4);
    }

    @Test
    void getAllBookings_Past() {
        List<BookingOutputDto> list2 = bookingService.findAllUsersBooking(booker.getId(), "Past");
        Assertions.assertThat(list2).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookings_Future() {
        List<BookingOutputDto> list3 = bookingService.findAllUsersBooking(booker.getId(), "Future");
        Assertions.assertThat(list3).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookings_Current() {
        List<BookingOutputDto> list4 = bookingService.findAllUsersBooking(booker.getId(), "Current");
        Assertions.assertThat(list4).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookings_Rejected() {
        List<BookingOutputDto> list5 = bookingService.findAllUsersBooking(booker.getId(), "Rejected");
        Assertions.assertThat(list5).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookings_Waiting() {
        List<BookingOutputDto> list6 = bookingService.findAllUsersBooking(booker.getId(), "waiting");
        Assertions.assertThat(list6).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_All() {
        List<BookingOutputDto> list1 = bookingService.findAllBookingsForItems(owner.getId(), "All");
        Assertions.assertThat(list1).isNotEmpty().hasSize(4);
    }

    @Test
    void getAllBookingsForOwner_Past() {
        List<BookingOutputDto> list2 = bookingService.findAllBookingsForItems(owner.getId(), "Past");
        Assertions.assertThat(list2).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_Future() {
        List<BookingOutputDto> list3 = bookingService.findAllBookingsForItems(owner.getId(), "Future");
        Assertions.assertThat(list3).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookingsForOwner_Current() {
        List<BookingOutputDto> list4 = bookingService.findAllBookingsForItems(owner.getId(), "Current");
        Assertions.assertThat(list4).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookingsForOwner_Rejected() {
        List<BookingOutputDto> list5 = bookingService.findAllBookingsForItems(owner.getId(), "Rejected");
        Assertions.assertThat(list5).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_Waiting() {
        List<BookingOutputDto> list6 = bookingService.findAllBookingsForItems(owner.getId(), "Waiting");
        Assertions.assertThat(list6).isNotEmpty().hasSize(1);
    }
}
