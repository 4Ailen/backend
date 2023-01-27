package com.aliens.friendship.chat.repository;

import org.springframework.data.repository.CrudRepository;
import com.aliens.friendship.chat.domain.Chat;

import java.util.List;

public interface ChatRepository extends CrudRepository<Chat, Long> {

    List<Chat> findAllByRoom(Long roomId);
}
