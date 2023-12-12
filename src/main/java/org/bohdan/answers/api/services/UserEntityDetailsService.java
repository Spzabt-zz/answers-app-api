package org.bohdan.answers.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.security.UserEntityDetails;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Service
public class UserEntityDetailsService implements UserDetailsService {

    UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return UserEntityDetails
                .builder()
                .userEntity(
                        userEntityRepository
                                .findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found!")))
                .build();
    }
}
