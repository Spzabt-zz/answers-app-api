package org.bohdan.answers.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    @Email(message = "Please, provide a valid email.")
    String email;

    @NotBlank(message = "Please, provide full name.")
    String fullName;

    @NotBlank(message = "Please, provide username.")
    String username;

    @NotBlank(message = "Please, provide phone number.")
    String phoneNumber;

    @NotBlank(message = "Please, enter password.")
    String password;

    @NotBlank(message = "Please, repeat password.")
    String repeatPassword;
}
