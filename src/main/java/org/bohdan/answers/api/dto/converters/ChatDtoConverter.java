package org.bohdan.answers.api.dto.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.dto.ChatDto;
import org.bohdan.answers.store.entities.ChatEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ChatDtoConverter {

    ModelMapper modelMapper;

    public ChatDto convertToChatDto(ChatEntity chat) {
        return modelMapper.map(chat, ChatDto.class);
    }
}
