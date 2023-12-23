package org.bohdan.answers.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.bohdan.answers.api.exceptions.NotFoundException;
import org.bohdan.answers.store.entities.Role;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
@Service
public class RegistrationService {

    final UserEntityRepository userEntityRepository;

    final PasswordEncoder passwordEncoder;

    final MailSenderService mailSenderService;

    @Value("${spring.profile.active}")
    private String profile;

    @Transactional
    public UserEntity register(UserEntity user, Role role) {

        UserEntity userEntity = userEntityRepository.save(
                UserEntity
                        .builder()
                        .email(user.getEmail())
                        .activationCode(UUID.randomUUID().toString())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber().replaceAll(" ", ""))
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(role)
                        .build()
        );

        String url = "dev".equals(profile) ? "http://localhost:8082" : "https://answers-ccff058443b8.herokuapp.com";

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s!\n" +
                            "Welcome to \"The Answers\" application. Please visit activation link: "+ url +"/api/v1/auth/activate/%s",
                    userEntity.getUsername(),
                    userEntity.getActivationCode()
            );

            // todo: FIX multiple emails being sent (when multiple requests are sent -> multiple transactions are started)
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }

        return userEntity;
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
