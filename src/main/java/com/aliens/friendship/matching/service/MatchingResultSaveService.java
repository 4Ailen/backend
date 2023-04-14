package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.domain.BlockingInfo;
import com.aliens.friendship.matching.domain.Matching;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.matching.repository.MatchingRepository;
import com.aliens.friendship.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.repository.ApplicantRepository;
import com.aliens.friendship.matching.service.model.Participant;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MatchingResultSaveService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final MatchingRepository matchingRepository;
    private final ApplicantRepository applicantRepository;
    private final MemberRepository memberRepository;
    private final BlockingInfoRepository blockingInfoRepository;

    public void saveMatchingResult(List<Participant> participants, Long numberOfMatches) {
        deleteAllMatchings();
        deleteAllChattingRooms();
        createChattingRooms(numberOfMatches);
        List<Matching> chattings = new ArrayList<>();

        for (Participant participant : participants) {

            Applicant applicant = applicantRepository.findById(participant.getId()).get();
            applicant.updateIsMatched(Applicant.Status.MATCHED);
            applicantRepository.save(applicant);

            List<com.aliens.friendship.matching.service.model.Matching> matchings = participant.getMatchingList();
            for (com.aliens.friendship.matching.service.model.Matching matching : matchings) {
                ChattingRoom chattingRoom = chattingRoomRepository.findById(matching.getChattingRoomId()).get();
                Applicant partner = applicantRepository.findById(matching.getPartner().getId()).get();
                Matching chatting = Matching.builder()
                        .chattingRoom(chattingRoom)
                        .applicant(partner)
                        .build();
                chattings.add(chatting);
            }
        }

        matchingRepository.saveAll(chattings);

        // 매칭 로직 진행 중 탈퇴한 회원 처리
        Applicant withDrawnApplicant = applicantRepository.findById(1).orElseThrow(() -> new NoSuchElementException("신청자가 아닙니다."));
        for (Participant participant : participants) {
            Member member = memberRepository.findById(participant.getId()).get();
            if (member.getIsWithdrawn() == Member.Status.WITHDRAWN) {
                Applicant applicant = applicantRepository.findById(participant.getId()).get();
                for (Matching matching : matchingRepository.findByApplicant(applicant)) {
                    matching.updateApplicant(withDrawnApplicant);
                    matchingRepository.save(matching);
                }
                applicantRepository.delete(applicant);
                deleteBlockingInfo(member);
                memberRepository.delete(member);
            }
        }
    }

    private void deleteBlockingInfo(Member member) {
        for (BlockingInfo blockingMember : blockingInfoRepository.findAllByBlockingMember(member)) {
            blockingInfoRepository.delete(blockingMember);
        }
        for (BlockingInfo blockedMember : blockingInfoRepository.findAllByBlockedMember(member)) {
            blockingInfoRepository.delete(blockedMember);
        }
    }

    private void createChattingRooms(Long numberOfChattingRooms){
        for(int i = 0; i < numberOfChattingRooms; i++){
            ChattingRoom chattingRoom = ChattingRoom.builder()
                    .id(Long.valueOf(i))
                    .status(ChattingRoom.RoomStatus.CLOSE)
                    .build();
            chattingRoomRepository.save(chattingRoom);
        }
    }

    private void deleteAllChattingRooms(){
        chattingRoomRepository.deleteAll();
    }

    private void deleteAllMatchings(){
        matchingRepository.deleteAll();
    }
}
