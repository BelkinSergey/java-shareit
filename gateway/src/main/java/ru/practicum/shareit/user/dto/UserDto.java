package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.group.Marker;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    long id;

    @NotBlank(message = "Имя пользователя не указано.", groups = Marker.OnCreate.class)
    private String name;

    @NotNull(message = "Почтовый адрес пустой.", groups = Marker.OnCreate.class)
    @Email(message = "Почтовый адрес не соответствует требованиям", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}





