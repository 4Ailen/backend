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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChattingService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private final ChattingRepository chattingRepository;
    private final ChatMessageRepository chatMessageRepository;




    public List<RoomInfoDto> getRoomInfoDtosByMatchingParticipantId(Integer currentMemberId) {
        List<RoomInfoDto> roomInfoDtos = new ArrayList<>();
        MatchingParticipant matchingParticipant = matchingParticipantRepository.findById(currentMemberId).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant "));
        for(Chatting chatting : chattingRepository.findByMatchingParticipant(matchingParticipant)){
            RoomInfoDto roomInfoDto = new RoomInfoDto();
            roomInfoDto.setRoomId(chatting.getChattingRoom().getId());
            roomInfoDto.setStatus(chatting.getChattingRoom().getStatus().toString());
            roomInfoDto.setPartnerId(chattingRepository.findPartnerIdByMatchingParticipantAndChattingRoom(matchingParticipant, chatting.getChattingRoom()));
            roomInfoDto.setChatMessages(findAllChatMessageByRoomId(chatting.getChattingRoom().getId()));
            roomInfoDtos.add(roomInfoDto);
        }
        return roomInfoDtos;
    }


    @Transactional
    public ChattingRoom createRoom() {
        return chattingRoomRepository.save(new ChattingRoom());
    }


    @Transactional
    public ChatMessage saveChatMessage(Long roomId, String sender, String message,Integer category) {
        // 실제 있는 roomId인지 검증
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException("Can't find room "+roomId));
        return chatMessageRepository.save(ChatMessage.createChat(room.getId(), sender, message,category));
    }


    @Transactional
    public void updateRoomStatus(Long roomId, String status) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(()-> new NoSuchElementException("Can't find room "+roomId));
        ChattingRoom.RoomStatus roomStatus = ChattingRoom.RoomStatus.valueOf(status.toUpperCase());
        room.updateStatus(roomStatus);
    }


    public List<ChatMessage> findAllChatMessageByRoomId(Long roomId) {
        return chatMessageRepository.findAllByRoom(roomId);
    }
}
