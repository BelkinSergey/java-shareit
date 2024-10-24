package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.group.Marker;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;

    @NotNull(message = "Элемент бронирования отсутствует.", groups = {Marker.OnCreate.class})
    private Long itemId;

    @NotNull(message = "Дата начала бронирования не указана.", groups = {Marker.OnCreate.class})
    @Future(message = "Дата начала бронирования указана в прошлом.", groups = {Marker.OnCreate.class})
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не указана.", groups = {Marker.OnCreate.class})
    @Future(message = "Дата окончания бронирования указана в прошлом.",
            groups = {Marker.OnCreate.class})
    private LocalDateTime end;


}