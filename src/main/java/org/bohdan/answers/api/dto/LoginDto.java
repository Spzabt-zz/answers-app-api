package org.bohdan.answers.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDto {

    @NotBlank(message = "Please, provide username.")
    String username;

    @NotBlank(message = "Please, enter password.")
    String password;
}
