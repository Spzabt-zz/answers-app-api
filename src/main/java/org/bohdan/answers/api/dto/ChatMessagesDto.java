package org.bohdan.answers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessagesDto {

    @JsonProperty("chat_messages")
    List<ChatMessageDto> chatMessages;
}
