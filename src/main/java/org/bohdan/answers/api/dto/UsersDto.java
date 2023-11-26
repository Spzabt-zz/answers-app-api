package org.bohdan.answers.api.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.domain.Views;
import org.bohdan.answers.store.entities.UserEntity;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersDto {

    @JsonView(Views.FullProfile.class)
    List<UserEntity> users;
}
