package org.bohdan.answers.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.repositories.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Service
public class ChatService {

    ChatRepository chatRepository;

    @Transactional
    public Optional<ChatEntity> getChatByUserId(Long userId) {

        return chatRepository.findByUserId(userId);
    }
}
