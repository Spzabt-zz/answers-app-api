package org.bohdan.answers.api.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.bohdan.answers.api.dto.JWTDto;
import org.bohdan.answers.api.dto.LoginDto;
import org.bohdan.answers.api.dto.UserActivationDto;
import org.bohdan.answers.api.dto.UserDto;
import org.bohdan.answers.api.dto.converters.UserDtoConverter;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.api.exceptions.UserDoesNotSignInException;
import org.bohdan.answers.api.exceptions.UserNotRegisteredException;
import org.bohdan.answers.api.security.JWTUtil;
import org.bohdan.answers.api.security.UserEntityDetails;
import org.bohdan.answers.api.services.RegistrationService;
import org.bohdan.answers.api.services.UserEntityDetailsService;
import org.bohdan.answers.api.utils.ControllerUtil;
import org.bohdan.answers.api.validator.UserEntityValidator;
import org.bohdan.answers.store.entities.Role;
import org.bohdan.answers.store.entities.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    RegistrationService registrationService;

    UserEntityValidator userEntityValidator;

    UserDtoConverter userDtoConverter;

    AuthenticationManager authenticationManager;

    JWTUtil jwtUtil;

    UserEntityDetailsService userEntityDetailsService;

    private static final String REGISTRATION = "/auth/registration";
    private static final String ACTIVATION = "/auth/activate/{code}";
    private static final String LOGIN = "/auth/login";

    @PostMapping(LOGIN)
    public ResponseEntity<JWTDto> performLogin(
            @RequestBody @Valid LoginDto loginDto,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            ArrayList<String> errors = ControllerUtil.bindErrors(bindingResult);

            throw new UserDoesNotSignInException(errors, "User not registered, because of fields errors: " + errors);
        }

        UserEntityDetails userDetails;
        try {
            // todo: handle exception in JWTFilter if token incorrect, because endpoint creates new JWTToken if previous was incorrect
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            userDetails = (UserEntityDetails) userEntityDetailsService.loadUserByUsername(loginDto.getUsername());
            if (Objects.nonNull(userDetails.getUserEntity().getActivationCode())) {
                throw new BadRequestException("Activate your account!");
            }
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Incorrect credentials!");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        JWTDto
                                .builder()
                                .id(userDetails.getUserEntity().getId())
                                .username(userDetails.getUsername())
                                .role(userDetails.getAuthorities()
                                        .stream()
                                        .map(role -> role.getAuthority())
                                        .reduce((acc, role) -> acc + ", " + role)
                                        .orElseGet(() -> Role.USER.name()))
                                .jwtToken(jwtUtil.generateToken(loginDto.getUsername()))
                                .build()
                );
    }

    @PostMapping(REGISTRATION)
    public ResponseEntity<JWTDto> performRegistration(
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        JWTDto
                                .builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .role(user.getRole().name())
                                .jwtToken(jwtUtil.generateToken(user.getUsername()))
                                .build()
                );
    }

    @GetMapping(ACTIVATION)
    public ResponseEntity<UserActivationDto> activate(@PathVariable("code") String activationCode) {

        boolean isActivated = registrationService.activateUser(activationCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        UserActivationDto
                                .builder()
                                .activationStatus(isActivated)
                                .build()
                );
    }
}
