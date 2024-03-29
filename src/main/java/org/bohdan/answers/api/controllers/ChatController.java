package org.bohdan.answers.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.dto.ChatDto;
import org.bohdan.answers.api.dto.converters.ChatDtoConverter;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.api.exceptions.NotFoundException;
import org.bohdan.answers.api.security.UserEntityDetails;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.ChatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

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
    public static final String GET_CHAT = "/chats/{chat_id}";
    public static final String GET_CHAT_BY_USER_ID = "/chats/{user_id}";

    @PutMapping(CREATE_CHAT)
    public ResponseEntity<ChatDto> createChat(@RequestParam(name = "user_id") Long userId) {
        UserEntity user = controllerHelper.getUserOrThrowException(userId);

        ChatEntity userChat = user.getChat();
        if (userChat != null)
            throw new BadRequestException(String.format("User \"%s\" already have a chat.", user.getId()));

        ChatEntity chat = chatRepository.save(
                ChatEntity
                        .builder()
                        .user(user)
                        .build()
        );

        user.setChat(chat);

        return new ResponseEntity<>(chatDtoConverter.convertToChatDto(chat), HttpStatus.CREATED);
    }

//    @GetMapping(GET_CHAT)
//    public ResponseEntity<ChatDto> getChatById(@PathVariable(name = "chat_id") Long chatId) {
//
//        ChatEntity chat = controllerHelper.getChatOrThrowException(chatId);
//
//        checkUserAccessToChatPermission(chatId, chat);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(
//                        ChatDto
//                                .builder()
//                                .id(chat.getId())
//                                .createdAt(chat.getCreatedAt())
//                                .build()
//                );
//    }

    @GetMapping(GET_CHAT_BY_USER_ID)
    public ResponseEntity<ChatDto> getChatByUserId(@PathVariable(name = "user_id") Long userId) {

        ChatEntity chat = chatRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Chat by user \"%s\", not found", userId)
                ));

        checkUserAccessToChatPermission(chat.getId(), chat);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ChatDto
                                .builder()
                                .id(chat.getId())
                                .createdAt(chat.getCreatedAt())
                                .build()
                );
    }

    private static void checkUserAccessToChatPermission(Long chatId, ChatEntity chat) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntityDetails userDetails = (UserEntityDetails) authentication.getPrincipal();

        if (!Objects.equals(chat.getUser().getUsername(), userDetails.getUsername()))
            throw new NotFoundException(String.format("Chat \"%s\" not found for current user.", chatId));
    }
}
