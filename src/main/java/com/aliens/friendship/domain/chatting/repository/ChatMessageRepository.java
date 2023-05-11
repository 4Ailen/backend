package com.aliens.friendship.domain.chatting.repository;

import org.springframework.data.repository.CrudRepository;
import com.aliens.friendship.domain.chatting.domain.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByRoom(Long roomId);
}
