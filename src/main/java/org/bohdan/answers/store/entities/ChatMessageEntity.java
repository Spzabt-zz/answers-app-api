package org.bohdan.answers.store.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "Please, enter your question.")
    @Column(name = "user_question", length = 1000)
    String userQuestion;

    @Column(name = "ai_response", length = 5000)
    String aiResponse;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    ChatEntity chat;
}
