package org.bohdan.answers.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.dto.ChatMessageDto;
import org.bohdan.answers.api.dto.ChatMessagesDto;
import org.bohdan.answers.api.dto.OpenaiRequestDto;
import org.bohdan.answers.api.dto.OpenaiResponseDto;
import org.bohdan.answers.api.dto.converters.ChatMessageDtoConverter;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.api.exceptions.NotFoundException;
import org.bohdan.answers.api.security.UserEntityDetails;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.entities.ChatMessageEntity;
import org.bohdan.answers.store.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@RestController
@RequestMapping("/api/v1")
public class ChatMessageController {

    final ChatMessageRepository chatMessageRepository;

    final ControllerHelper controllerHelper;

    final ChatMessageDtoConverter chatMessageDtoConverter;

    final RestTemplate restTemplate;

    @Value("${openai.model}")
    String model;

    @Value("${openai.api.url}")
    String apiUrl;

    private static final String SEND_MESSAGE = "/{chat_id}/chat_messages";
    private static final String FETCH_MESSAGES = "/{chat_id}/chat_messages";

    @PostMapping(SEND_MESSAGE)
    public ChatMessageDto sendMessage(
            @RequestParam(name = "user_question") String userQuestion,
            @PathVariable(name = "chat_id") Long chatId
    ) {

        ChatEntity chat = controllerHelper.getChatOrThrowException(chatId);

        checkUserAccessToChatPermission(chatId, chat);

        OpenaiRequestDto request = new OpenaiRequestDto(model, userQuestion);

        OpenaiResponseDto response = restTemplate.postForObject(apiUrl, request, OpenaiResponseDto.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new BadRequestException("No response");
        }

        String content = response.getChoices().get(0).getMessage().getContent();

        ChatMessageEntity chatMessage = chatMessageRepository.save(
                ChatMessageEntity
                        .builder()
                        .chat(chat)
                        .userQuestion(userQuestion)
                        .aiResponse(content)
                        .build()
        );

        chat.setChatMessages(new ArrayList<>(Collections.singletonList(chatMessage)));

        return chatMessageDtoConverter.convertToChatMessageDto(chatMessage);
    }

    @GetMapping(FETCH_MESSAGES)
    public ChatMessagesDto fetchMessages(@PathVariable(name = "chat_id") Long chatId) {

        ChatEntity chat = controllerHelper.getChatOrThrowException(chatId);

        checkUserAccessToChatPermission(chatId, chat);

        return ChatMessagesDto
                .builder()
                .chatMessages(
                        chatMessageRepository
                                .streamAllByChatId(chatId)
                                .map(chatMessageDtoConverter::convertToChatMessageDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private static void checkUserAccessToChatPermission(Long chatId, ChatEntity chat) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntityDetails userDetails = (UserEntityDetails) authentication.getPrincipal();

        if (!Objects.equals(chat.getUser().getUsername(), userDetails.getUsername()))
            throw new NotFoundException(String.format("Chat \"%s\" not found for current user.", chatId));
    }
}
