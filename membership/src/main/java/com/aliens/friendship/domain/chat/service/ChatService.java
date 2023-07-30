package com.aliens.friendship.domain.chat.service;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.chatting.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingJwtTokenUtil chattingJwtTokenUtil;

    @Transactional
    public void closeChattingRoom(ChattingRoomEntity chattingRoomEntity) {
        chattingRoomEntity.updateStatus(ChattingRoomEntity.RoomStatus.CLOSE);
    }

    @Transactional
    public ChattingRoomEntity register(ChattingRoomEntity chattingRoomEntity) {
        return chattingRoomRepository.save(chattingRoomEntity);
    }

    public ChattingRoomEntity findChattingRoomById(Long roomId) {
        ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElseThrow(()-> new NoSuchElementException("Can't find room "+roomId));
        return chattingRoomEntity;
    }

    public String generateTokenWithMemberIdAndRoomIds(Long memberId, List<Long> roomIds) {
        return chattingJwtTokenUtil.generateToken(memberId,roomIds);
    }
}