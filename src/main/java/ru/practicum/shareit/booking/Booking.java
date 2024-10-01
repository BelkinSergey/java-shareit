package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Getter
@Setter
public class Booking {
    private long id;

    @NotNull(message = "Элемент бронирования отсутствует.")
    private Item item;

    @NotNull(message = "Дата начала бронирования не указана.")
    private LocalDate start;

    @NotNull(message = "Дата окончания бронирования не указана.")
    private LocalDate end;

    @NotNull(message = "Пользователь, желающий забронировать вещь, отсутствует.")
    private User booker;

    private BookingStatus status;


}
