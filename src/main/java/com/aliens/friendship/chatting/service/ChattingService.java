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
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
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

    @Transactional(readOnly = false)
    public void updateRoomStatus(int roomId, String status) {
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
            roomInfoDto.setPartnerId(matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicant, chattingRoom));
            roomInfoDtoList.add(roomInfoDto);
        }
        return roomInfoDtoList;
    }
}
