package org.bohdan.answers.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.dto.ChatDto;
import org.bohdan.answers.api.dto.converters.ChatDtoConverter;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.ChatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
@RequestMapping("/api/v1")
public class ChatController {

    ChatRepository chatRepository;

    ChatDtoConverter chatDtoConverter;

    ControllerHelper controllerHelper;

    public static final String FETCH_CHATS = "/api/v1/chats";
    public static final String CREATE_OR_UPDATE_CHAT = "/api/v1/chats";
    public static final String CREATE_CHAT = "/chats";
    public static final String DELETE_CHAT = "/api/v1/chats/{chat_id}";
    public static final String GET_CHAT = "/api/v1/chats/{chat_id}";

    @PutMapping(CREATE_CHAT)
    public ResponseEntity<ChatDto> createOrUpdateChat(@RequestParam(name = "user_id") Long userId) {
        UserEntity user = controllerHelper.getUserOrThrowException(userId);

        ChatEntity chat = chatRepository.save(
                ChatEntity
                        .builder()
                        .user(user)
                        .build()
        );

        return new ResponseEntity<>(chatDtoConverter.convertToChatDto(chat), HttpStatus.OK);
    }
}