package org.bohdan.answers.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.store.entities.Role;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Service
public class RegistrationService {

    UserEntityRepository userEntityRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserEntity user, Role role) {

        userEntityRepository.save(
                UserEntity
                        .builder()
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(role)
                        .build()
        );
    }
}
