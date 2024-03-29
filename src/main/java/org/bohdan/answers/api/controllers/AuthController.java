package org.bohdan.answers.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.dto.*;
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
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
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

    ControllerHelper controllerHelper;


    private static final String REGISTRATION = "/auth/registration";
    private static final String ACTIVATION = "/auth/activate/{code}";
    private static final String LOGIN = "/auth/login";
    private static final String REST_PASSWORD = "/auth/reset-password";
    private static final String LOGOUT = "/auth/logout";
    private static final String CHECK_TOKEN = "/auth/check-token";
    private static final String CHECK_USER_ACTIVATION = "/auth/check-user-activation";

    @PostMapping(LOGIN)
    public ResponseEntity<JWTDto> performLogin(
            @RequestBody @Valid LoginDto loginDto,
            BindingResult bindingResult
    ) {

        // TODO: implement remember me
        if (bindingResult.hasErrors()) {
            ArrayList<String> errors = ControllerUtil.bindErrors(bindingResult);

            throw new UserDoesNotSignInException(errors, "User not registered, because of fields errors: " + errors);
        }

        UserEntityDetails userDetails;
        try {
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

        user = registrationService.register(user, Role.USER);

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

    @GetMapping(CHECK_TOKEN)
    public ResponseEntity<JWTExpiryValidationDto> checkJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        boolean isExpired;
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.split(" ")[1];
            isExpired = jwtUtil.isJWTExpired(jwt);
        } else {
            throw new BadRequestException("Invalid header!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        JWTExpiryValidationDto
                                .builder()
                                .isJwtTokenExpired(isExpired)
                                .build()
                );
    }

    @GetMapping(CHECK_USER_ACTIVATION)
    public ResponseEntity<UserActivationDto> checkUserActivation(@RequestParam(name = "user_id") Long userId) {

        UserEntity user = controllerHelper.getUserOrThrowException(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        UserActivationDto
                                .builder()
                                .activationStatus(!Objects.nonNull(user.getActivationCode()))
                                .build()
                );
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<HttpStatus> logout(@RequestHeader("Authorization") String token) {
        String jwt = extractToken(token);

        // todo: implement jwt blacklist
        if (jwt == null)
            throw new BadRequestException("Blank JWT");
        //tokenBlacklistService.addToBlacklist(jwt);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(REST_PASSWORD)
    public ResponseEntity<JWTDto> resetPassword(@RequestParam("email") String email,
                                                @RequestParam("old_password") String password,
                                                @RequestParam("new_password") String newPassword
    ) {
        UserEntity user = controllerHelper.getUserByEmailOrThrowException(email);
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.hasText(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.hasText(password)) {
            user.setPassword(password);
        }

        registrationService.resetPassword(user, isEmailChanged);

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

    private String extractToken(String header) {
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
