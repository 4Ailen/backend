package com.aliens.friendship.service;

import com.aliens.friendship.domain.Chat;
import com.aliens.friendship.domain.Chatting;
import com.aliens.friendship.domain.ChattingRoom;
import com.aliens.friendship.domain.MatchingParticipant;
import com.aliens.friendship.domain.dto.RoomInfoDto;
import com.aliens.friendship.repository.ChattingRepository;
import com.aliens.friendship.repository.ChattingRoomRepository;
import com.aliens.friendship.repository.MatchingParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aliens.friendship.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private final ChattingRepository chattingRepository;
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
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException("Can't find room "+roomId));
        return chatRepository.save(Chat.createChat(room.getId(), sender, message));
    }

    /**
     * 채팅방 채팅내용 불러오기
     * @param roomId 채팅방 id
     */
    public List<Chat> findAllChatByRoomId(Long roomId) {
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
        for(Chatting chatting : chattingRepository.findByChattingRoom(chattingRoom)){
            if(chatting.getMatchingParticipant().getId() != matchingParticipant.getId()){
                return chatting.getMatchingParticipant().getId();
            }
        }
        return null;
    }
}
