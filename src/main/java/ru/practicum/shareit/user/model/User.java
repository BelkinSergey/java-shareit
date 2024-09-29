package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Имя пользователя не указано.")
    private String name;

    @NotNull(message = "Почтовый адрес пустой.")
    @Email(message = "Почтовый адрес не соответствует требованиям")
    private String email;
}