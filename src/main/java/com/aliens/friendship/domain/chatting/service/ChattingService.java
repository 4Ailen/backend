package com.aliens.friendship.domain.chatting.service;

import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import com.aliens.friendship.domain.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Matching;
import com.aliens.friendship.domain.auth.dto.RoomInfoDto;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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
    private final ChattingJwtTokenUtil chattingJwtTokenUtil;
    private final MemberRepository memberRepository;

    public String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Transactional
    public String getJWTTokenForChatting() {
        Integer currentMemberId = memberRepository.findByEmail(getCurrentMemberEmail()).get().getId();
        Applicant applicant = applicantRepository.findById(currentMemberId).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant " + currentMemberId));
        List<Matching> matchings = matchingRepository.findByApplicant(applicant);
        ArrayList<Long> roomIds = new ArrayList<>();
        for( Matching matching : matchings){
            roomIds.add(matching.getChattingRoom().getId());
        }
        return chattingJwtTokenUtil.generateToken(currentMemberId,roomIds);
    }


    @Transactional
    public void updateRoomStatus(Long roomId, String status) {
        ChattingRoom room = chattingRoomRepository.findById(roomId).orElseThrow(()-> new NoSuchElementException("Can't find room "+roomId));
        ChattingRoom.RoomStatus roomStatus = ChattingRoom.RoomStatus.valueOf(status.toUpperCase());
        room.updateStatus(roomStatus);
        chattingRoomRepository.save(room);
    }

    public List<RoomInfoDto> getRoomInfoDtoListByMatchingCurrentMemberId() {
        Integer currentMemberId = memberRepository.findByEmail(getCurrentMemberEmail()).get().getId();
        List<RoomInfoDto> roomInfoDtoList = new ArrayList<>();
        Applicant applicant = applicantRepository.findById(currentMemberId).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant " + currentMemberId));
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

    @Transactional
    public void blockChattingRoom(Long roomId) {
        updateRoomStatus(roomId, "CLOSE");
    }

}