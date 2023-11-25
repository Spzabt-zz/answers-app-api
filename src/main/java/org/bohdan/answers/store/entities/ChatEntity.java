package org.bohdan.answers.store.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Builder.Default
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    Instant createdAt = Instant.now();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "chat")
    List<ChatMessageEntity> chatMessages = new ArrayList<>();
}
