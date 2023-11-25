package org.bohdan.answers.api.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.controllers.helpers.ControllerHelper;
import org.bohdan.answers.api.dto.ChatMessageDto;
import org.bohdan.answers.api.dto.OpenaiRequestDto;
import org.bohdan.answers.api.dto.OpenaiResponseDto;
import org.bohdan.answers.api.dto.converters.ChatMessageDtoConverter;
import org.bohdan.answers.api.exceptions.BadRequestException;
import org.bohdan.answers.store.entities.ChatEntity;
import org.bohdan.answers.store.entities.ChatMessageEntity;
import org.bohdan.answers.store.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

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
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final static String SEND_MESSAGE = "/{chat_id}/chat_messages";

    @Autowired
    public ChatMessageController(ChatMessageRepository chatMessageRepository, ControllerHelper controllerHelper, ChatMessageDtoConverter chatMessageDtoConverter, RestTemplate restTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.controllerHelper = controllerHelper;
        this.chatMessageDtoConverter = chatMessageDtoConverter;
        this.restTemplate = restTemplate;
    }

    @PostMapping(SEND_MESSAGE)
    public ChatMessageDto sendMessage(
            @RequestParam(name = "user_question") String userQuestion,
            @PathVariable(name = "chat_id") Long chatId
    ) {
        ChatEntity chat = controllerHelper.getChatOrThrowException(chatId);

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
}
