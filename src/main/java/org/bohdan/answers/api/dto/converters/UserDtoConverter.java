package org.bohdan.answers.api.dto.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.dto.UserDto;
import org.bohdan.answers.store.entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class UserDtoConverter {

    ModelMapper modelMapper;

    public UserEntity convertToUserEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
