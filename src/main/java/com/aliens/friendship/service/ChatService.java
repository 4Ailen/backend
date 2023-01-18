package com.aliens.friendship.service;

import com.aliens.friendship.domain.Chat;
import com.aliens.friendship.domain.ChattingRoom;
import com.aliens.friendship.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aliens.friendship.repository.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChatRepository chatRepository;

    /**
     * 모든 채팅방 찾기
     */
    public List<ChattingRoom> findAllRoom() {
        return chattingRoomRepository.findAll();
    }

    /**
     * 특정 채팅방 찾기
     * @param id room_id
     */
    public ChattingRoom findRoomById(Integer id) {
        return chattingRoomRepository.findById(id).orElseThrow();
    }

    /**
     * 채팅방 만들기
     * @param name 방 이름
     */
    public ChattingRoom createRoom() {
        return chattingRoomRepository.save(new ChattingRoom());
    }

    /////////////////

    /**
     * 채팅 생성
     * @param roomId 채팅방 id
     * @param sender 보낸이
     * @param message 내용
     */
    public Chat createChat(Integer roomId, String sender, String message) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow();  //방 찾기 -> 없는 방일 경우 여기서 예외처리
        return chatRepository.save(Chat.createChat(room.getId(), sender, message));
    }

    /**
     * 채팅방 채팅내용 불러오기
     * @param roomId 채팅방 id
     */
    public List<Chat> findAllChatByRoomId(Long roomId) {
        return chatRepository.findAllByRoom(roomId);
    }


}
