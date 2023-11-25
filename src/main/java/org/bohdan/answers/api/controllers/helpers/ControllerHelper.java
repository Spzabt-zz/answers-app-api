package org.bohdan.answers.api.controllers.helpers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.exceptions.NotFoundException;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.entities.UserEntity;
import org.bohdan.answers.store.repositories.ChatRepository;
import org.bohdan.answers.store.repositories.UserEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ControllerHelper {

    UserEntityRepository userRepository;

    ChatRepository chatRepository;

    public UserEntity getUserOrThrowException(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "User with \"%s\" doesn't exist.",
                                        userId
                                )
                        )
                );
    }

    public ChatEntity getChatOrThrowException(Long chatId) {

        return chatRepository
                .findById(chatId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Chat with \"%s\" doesn't exist.",
                                        chatId
                                )
                        )
                );
    }
}
