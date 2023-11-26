package org.bohdan.answers.store.repositories;

import org.bohdan.answers.store.entities.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    Stream<ChatMessageEntity> streamAllByChatId(Long chatId);
}
