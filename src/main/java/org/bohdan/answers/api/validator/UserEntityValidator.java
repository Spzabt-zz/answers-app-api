package org.bohdan.answers.api.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Component
public class UserEntityValidator implements Validator {

    UserEntityRepository userEntityRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserEntity person = (UserEntity) target;

        if (!Objects.equals(person.getPassword(), person.getRepeatPassword())) {
            //throw new BadRequestException("Passwords doesn't match.");
            errors.rejectValue("password", "", "Passwords doesn't match");
        }

        if (userEntityRepository.findByFullName(person.getFullName()).isPresent()) {
            //throw new BadRequestException("Such name already in the system.");
            errors.rejectValue("full_name", "", "Such name already in the system");
        }
    }
}
