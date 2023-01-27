package com.aliens.friendship.chatting.repository;

import org.springframework.data.repository.CrudRepository;
import com.aliens.friendship.chatting.domain.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByRoom(Long roomId);
}
