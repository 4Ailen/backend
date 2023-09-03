package com.aliens.friendship.domain.match.business;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.applicant.repository.ApplicantRepository;
import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.match.service.MatchService;
import com.aliens.friendship.domain.match.service.model.Participant;
import com.aliens.friendship.domain.match.service.model.ServiceModelMatching;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Business
@RequiredArgsConstructor
public class MatchSaveBusiness {

    private final ChatService chatService;
    private final ApplicantService applicantService;
    private final MemberService memberService;
    private final MatchService matchService;
    private final ApplicantRepository applicantRepository;

    public void saveMatchingResult(List<Participant> participants) throws Exception {
        ApplicantEntity tmpApplicantEntity = applicantService.findByMemberEntity(memberService.findById(participants.get(0).getId()));
        Instant matchingDate = applicantService.getDateWillMatched(tmpApplicantEntity);

        for (Participant participant : participants) {
            // 신청자 엔티티
            ApplicantEntity nowApplicantEntity = applicantService.findByMemberEntity(memberService.findById(participant.getId()));

            // 신청자 매칭완료 상태변경
            applicantService.updateIsMatched(nowApplicantEntity);

            // 멤버 엔티티 매칭완료 상태변경
            memberService.updateMatched(nowApplicantEntity.getMemberEntity());

            // 매칭 객체리스트
            List<ServiceModelMatching> serviceModelMatchings = participant.getServiceModelMatchingList();

            // 매칭 주인으로 저장될 멤버엔티티
            MemberEntity matchingMemberEntity = memberService.findById(participant.getId());

            for (ServiceModelMatching serviceModelMatching : serviceModelMatchings) {
                // 매칭 파트너로 저장될 멤버엔티티
                MemberEntity matchedMemberEntity = memberService.findById(serviceModelMatching.getPartner().getId());

                // 매칭 파트너가 주인이고 매칭 주인이 파트너인 매칭 엔티티 조회
                Optional<MatchingEntity> partnerIsMasterMatchEntity = matchService.findByMatchedMemberAndMatchingMemberReverseWithMatchingDate(matchedMemberEntity,matchingMemberEntity,matchingDate);

                //매칭 파트너가 주인인 매칭 엔티티가 있을 경우,
                // 저장된 채팅룸으로 매칭 엔티티에 저장
                if(partnerIsMasterMatchEntity.isPresent()){
                    MatchingEntity matchingEntity = MatchingEntity.builder()
                            .matchingMember(matchingMemberEntity)
                            .matchedMember(matchedMemberEntity)
                            .matchingDate(matchingDate)
                            .chattingRoomEntity(partnerIsMasterMatchEntity.get().getChattingRoomEntity()).build();
                    matchService.save(matchingEntity);
                }

                //매칭 파트너가 주인인 매칭 엔티티가 없을 경우,
                // 새로운 채팅룸엔티티를 매칭 엔티티에 저장
                else{
                    ChattingRoomEntity chattingRoomEntity = ChattingRoomEntity.builder()
                            .status(ChattingRoomEntity.RoomStatus.OPEN)
                            .build();
                    ChattingRoomEntity chattingRoom = chatService.register(chattingRoomEntity);

                    MatchingEntity matchingEntity = MatchingEntity.builder()
                            .matchingMember(matchingMemberEntity)
                            .matchedMember(matchedMemberEntity)
                            .matchingDate(matchingDate)
                            .chattingRoomEntity(chattingRoom).build();
                    matchService.save(matchingEntity);
                }
            }
        }

        updateAllApplicantsIsMatchedToMatched();
    }

    private void updateAllApplicantsIsMatchedToMatched() {
        List<ApplicantEntity> allParticipants = applicantService.findAllParticipants();
        for(ApplicantEntity applicantEntity : allParticipants) {
            applicantEntity.updateIsMatched(ApplicantEntity.Status.MATCHED);
        }
        applicantRepository.saveAll(allParticipants);
    }
}
