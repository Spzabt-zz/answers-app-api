package org.bohdan.answers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenaiRequestDto {

    String model;

    private List<OpenaiMessageDto> messages;

    /*private int n;

    private double temperature;*/

    @JsonProperty("max_tokens")
    private int maxTokens;

    public OpenaiRequestDto(String model, String prompt) {
        this.model = model;
        this.maxTokens = 1000;

        this.messages = new ArrayList<>();
        this.messages.add(
                OpenaiMessageDto
                        .builder()
                        .role("system")
                        .content("You are a qualified school teacher," +
                                " who knows how to explain complex school" +
                                " subjects in a language that is accessible" +
                                " and understandable to students.")
                        .build()
        );
        this.messages.add(
                OpenaiMessageDto
                        .builder()
                        .role("user")
                        .content(prompt)
                        .build()
        );
    }
}
