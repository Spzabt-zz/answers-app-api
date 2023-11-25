package org.bohdan.answers.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageDto {

    @NotBlank(message = "Please, enter your question.")
    String userQuestion;

    String aiResponse;
}
