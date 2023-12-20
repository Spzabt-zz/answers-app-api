package org.bohdan.answers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JWTExpiryValidationDto {

    @JsonProperty("is_jwt_token_expired")
    Boolean isJwtTokenExpired;
}
