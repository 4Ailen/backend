package com.aliens.friendship.chatting.service;

import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.chatting.domain.Chatting;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.aliens.friendship.jwt.domain.dto.RoomInfoDto;
import com.aliens.friendship.chatting.repository.ChattingRepository;
import com.aliens.friendship.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aliens.friendship.chatting.repository.ChatMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private final ChattingRepository chattingRepository;
    private final ChatMessageRepository chatRepository;

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
    public ChatMessage createChat(Integer roomId, String sender, String message) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException("Can't find room "+roomId));
        return chatRepository.save(ChatMessage.createChat(room.getId(), sender, message));
    }

    /**
     * 채팅방 채팅내용 불러오기
     * @param roomId 채팅방 id
     */
    public List<ChatMessage> findAllChatByRoomId(Long roomId) {
        return chatRepository.findAllByRoom(roomId);
    }

    public void updateRoomStatus(int roomId, String status) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(()-> new NoSuchElementException("Can't find room "+roomId));
        ChattingRoom.RoomStatus roomStatus = ChattingRoom.RoomStatus.valueOf(status.toUpperCase());
        room.updateStatus(roomStatus);
        chattingRoomRepository.save(room);
    }

    public List<RoomInfoDto> getRoomInfoDtoListByMatchingParticipantId(Integer matchingParticipantId) {
        List<RoomInfoDto> roomInfoDtoList = new ArrayList<>();
        MatchingParticipant matchingParticipant = matchingParticipantRepository.findById(matchingParticipantId).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant " + matchingParticipantId));
        for(Chatting chatting : chattingRepository.findByMatchingParticipant(matchingParticipant)){
            RoomInfoDto roomInfoDto = new RoomInfoDto();
            roomInfoDto.setRoomId(chatting.getChattingRoom().getId());
            roomInfoDto.setStatus(chatting.getChattingRoom().getStatus().toString());
            roomInfoDto.setPartnerId(findPartnerId(matchingParticipant, chatting.getChattingRoom()));
            roomInfoDtoList.add(roomInfoDto);
        }
        return roomInfoDtoList;
    }

    private Integer findPartnerId(MatchingParticipant matchingParticipant, ChattingRoom chattingRoom) {

        return chattingRepository.findChattingByMatchingParticipantAndChattingRoom(matchingParticipant, chattingRoom);
    }
}
