package org.bohdan.answers.api.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.domain.Views;
import org.bohdan.answers.api.dto.UserDto;
import org.bohdan.answers.api.dto.UsersDto;
import org.bohdan.answers.api.dto.converters.UserDtoConverter;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
@RequestMapping("/api/v1")
public class UsersController {

    UserEntityRepository userRepository;

    UserDtoConverter userDtoConverter;

    ControllerHelper controllerHelper;

    //ActiveUserStore activeUserStore;

    private static final String FETCH_USERS = "/users";
    private static final String FETCH_USER = "/user";

    @GetMapping(FETCH_USERS)
    @JsonView(Views.FullProfile.class)
    public UsersDto fetchUsers() {

        return UsersDto
                .builder()
                .users(userRepository.findAll())
                .build();
    }

    @GetMapping(FETCH_USER)
    public UserDto fetchUsers(@RequestParam("user_id") Long userId) {
        UserEntity user = controllerHelper.getUserOrThrowException(userId);

        return UserDto
                .builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }
}
