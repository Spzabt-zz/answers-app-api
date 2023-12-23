package org.bohdan.answers.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    @Email(message = "Please, provide a valid email.")
    @NotBlank(message = "Please, provide email.")
    @Size(min = 0, max = 100, message = "Email should be between 2 and 100 characters")
    String email;

    @NotBlank(message = "Please, provide full name.")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 characters")
    String fullName;

    @NotBlank(message = "Please, provide username.")
    @Size(min = 2, max = 100, message = "Username should be between 2 and 100 characters")
    String username;

    @NotBlank(message = "Please, provide phone number.")
    @Size(min = 5, max = 15, message = "Phone number should be between 5 and 15 characters")
    @Pattern(regexp = "^\\+[0-9 ]+$",
            message = "Invalid phone number")
    String phoneNumber;

    @NotBlank(message = "Please, enter password.")
    @Size(min = 8, max = 15, message = "Password should be between 8 and 15 characters")
    String password;

    @NotBlank(message = "Please, repeat password.")
    String repeatPassword;
}
