package org.bohdan.answers.api.dto.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.dto.ChatMessageDto;
import org.bohdan.answers.store.entities.ChatMessageEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ChatMessageDtoConverter {

    ModelMapper modelMapper;

    public ChatMessageDto convertToChatMessageDto(ChatMessageEntity chatMessage) {
        return modelMapper.map(chatMessage, ChatMessageDto.class);
    }
}
