package org.bohdan.answers.api.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.bohdan.answers.api.dto.UserDto;
import org.bohdan.answers.api.dto.converters.UserDtoConverter;
import org.bohdan.answers.api.exceptions.UserNotRegisteredException;
import org.bohdan.answers.api.services.RegistrationService;
import org.bohdan.answers.api.utils.ControllerUtil;
import org.bohdan.answers.api.validator.UserEntityValidator;
import org.bohdan.answers.store.entities.Role;
import org.bohdan.answers.store.entities.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    RegistrationService registrationService;

    UserEntityValidator userEntityValidator;

    UserDtoConverter userDtoConverter;

    private final static String REGISTRATION = "/auth/registration";

    @PostMapping(REGISTRATION)
    public ResponseEntity<HttpStatus> performRegistration(
            @RequestBody @Valid UserDto userDto,
            BindingResult bindingResult
    ) {

        UserEntity user = userDtoConverter.convertToUserEntity(userDto);

        userEntityValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            ArrayList<String> errors = ControllerUtil.bindErrors(bindingResult);

            throw new UserNotRegisteredException(errors, "User not registered, because of fields errors: " + errors);
        }

        registrationService.register(user, Role.USER);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
