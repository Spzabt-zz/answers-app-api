package org.bohdan.answers.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.bohdan.answers.api.exceptions.NotFoundException;
import org.bohdan.answers.store.entities.Role;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Service
public class RegistrationService {

    UserEntityRepository userEntityRepository;

    PasswordEncoder passwordEncoder;

    MailSenderService mailSenderService;

    @Transactional
    public void register(UserEntity user, Role role) {

        UserEntity userEntity = userEntityRepository.save(
                UserEntity
                        .builder()
                        .email(user.getEmail())
                        .activationCode(UUID.randomUUID().toString())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(role)
                        .build()
        );

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s!\n" +
                            "Welcome to \"The Answers\" application. Please visit activation link: http://localhost:8081/api/v1/auth/activate/%s",
                    userEntity.getUsername(),
                    userEntity.getActivationCode()
            );

            mailSenderService.send(user.getEmail(), "Activation code", message);
        }
    }

    @Transactional
    public boolean activateUser(String code) {
        UserEntity user = userEntityRepository.findByActivationCode(code)
                .orElseThrow(() ->
                        new NotFoundException("User already activated.")
                );

        user.setActivationCode(null);

        return true;
    }
}
