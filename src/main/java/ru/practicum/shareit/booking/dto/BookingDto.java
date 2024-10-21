package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;

    @NotNull(message = "Элемент бронирования отсутствует.")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования не указана.")
    @FutureOrPresent(message = "Дата начала бронирования указана в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не указана.")
    @FutureOrPresent(message = "Дата окончания бронирования указана в прошлом.")
    private LocalDateTime end;

    private Long bookerId;

    private BookingStatus status;
}
