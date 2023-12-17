package org.bohdan.answers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JWTDto {

    Long id;

    String username;

    String role;

    @JsonProperty("jwt_token")
    String jwtToken;
}
