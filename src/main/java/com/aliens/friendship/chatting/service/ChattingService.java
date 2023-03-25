package com.aliens.friendship.chatting.service;

import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.domain.Matching;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.jwt.domain.dto.RoomInfoDto;
import com.aliens.friendship.matching.repository.MatchingRepository;
import com.aliens.friendship.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.matching.repository.ApplicantRepository;
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
    private final ApplicantRepository applicantRepository;
    private final MatchingRepository matchingRepository;
    private final ChatMessageRepository chatMessageRepository;


    public ChattingRoom findRoomById(Long id) {
        return chattingRoomRepository.findById(id).orElseThrow();
    }


    @Transactional
    public ChattingRoom createRoom() {
        ChattingRoom room = new ChattingRoom(4040L, ChattingRoom.RoomStatus.PENDING);
        return chattingRoomRepository.save(room);
    }

    @Transactional
    public ChatMessage saveChatMessage(Long roomId, String sender, String message,Integer category) {
        // 실제 있는 roomId인지 검증
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException("Can't find room "+roomId));
        return chatMessageRepository.save(ChatMessage.createChat(room.getId(), sender, message,category));
    }


    public List<ChatMessage> findAllChatByRoomId(Long roomId) {
        return chatMessageRepository.findAllByRoom(roomId);
    }

    @Transactional
    public void updateRoomStatus(Long roomId, String status) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(()-> new NoSuchElementException("Can't find room "+roomId));
        ChattingRoom.RoomStatus roomStatus = ChattingRoom.RoomStatus.valueOf(status.toUpperCase());
        room.updateStatus(roomStatus);
        chattingRoomRepository.save(room);
    }

    public List<RoomInfoDto> getRoomInfoDtoListByMatchingParticipantId(Integer matchingParticipantId) {
        List<RoomInfoDto> roomInfoDtoList = new ArrayList<>();
        Applicant applicant = applicantRepository.findById(matchingParticipantId).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant " + matchingParticipantId));
        for(Matching matching : matchingRepository.findByApplicant(applicant)){
            RoomInfoDto roomInfoDto = new RoomInfoDto();
            ChattingRoom chattingRoom = matching.getChattingRoom();
            roomInfoDto.setRoomId(chattingRoom.getId());
            roomInfoDto.setStatus(chattingRoom.getStatus().toString());
            roomInfoDto.setChatMessages(chatMessageRepository.findAllByRoom(chattingRoom.getId()));
            roomInfoDto.setPartnerId(matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicant, chattingRoom));
            roomInfoDtoList.add(roomInfoDto);
        }
        return roomInfoDtoList;
    }

    @Transactional
    public void blockChattingRoom(Long roomId) {
        saveChatMessage(roomId, "공지", "차단된 상대입니다.", 3);
        updateRoomStatus(roomId, "CLOSE");
    }
}
