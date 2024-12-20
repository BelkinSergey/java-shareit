package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private static final String URL = "http://localhost:8080/users";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "Nick", "nick@mail.ru");

    @AfterEach
    void deleteUser() {
        userService.removeUserById(anyLong());
    }

    @Test
    void succeedCreateUser() throws Exception {
        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$.name", Matchers.is(userDto.getName())),
                        jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void succeedUpdateUserNameAndEmail() throws Exception {
        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);

        mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$.name", Matchers.is(userDto.getName())),
                        jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void succeedUpdateUsersName() throws Exception {
        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);

        mockMvc.perform(patch(URL + "/1")
                        .content("{" +
                                "    \"name\": \"Nick\"" +
                                " }")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$.name", Matchers.is(userDto.getName())),
                        jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void succeedUpdateUsersEmail() throws Exception {
        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);

        mockMvc.perform(patch(URL + "/1")
                        .content("{" +
                                "    \"email\": \"nick@mail.ru\"" +
                                " }")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$.name", Matchers.is(userDto.getName())),
                        jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void succeedFindById() throws Exception {
        when(userService.findUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get(URL + "/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$.name", Matchers.is(userDto.getName())),
                        jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void succeedDeleteUser() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .removeUserById(anyLong());
    }

    @Test
    void findAllWithUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", Matchers.is(userDto.getId()), Long.class),
                        jsonPath("$[0].name", Matchers.is(userDto.getName())),
                        jsonPath("$[0].email", Matchers.is(userDto.getEmail()))
                );
    }

    @Test
    void findAllWhenUsersListIsEmpty() throws Exception {
        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("[]")
                );
    }
}

