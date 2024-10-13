package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    long id;

    @NotBlank(message = "Имя пользователя не указано.")
    private String name;

    @NotNull(message = "Почтовый адрес пустой.")
    @Email(message = "Почтовый адрес не соответствует требованиям")
    private String email;
}

