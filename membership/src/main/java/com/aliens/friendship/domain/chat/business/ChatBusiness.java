package com.aliens.friendship.domain.chat.business;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.chat.converter.ChatConverter;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.match.business.MatchBusiness;
import com.aliens.friendship.domain.match.service.MatchService;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class ChatBusiness {

    private final ChatService chatService;
    private final MatchService matchService;
    private final ChatConverter chatConverter;
    private final MemberService memberService;
    private  final MatchBusiness matchBusiness;

    /**
     * Chatting server 용 토큰 발급
     */
    public String getJwtForChattingServer() throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 열린 상태의 채팅방을 가진 매칭 엔티티 리스트
        List<MatchingEntity> matchEntities = matchService.findMatchEntityWithOpenedChattingRoomByMemberEntity(loginMemberEntity);

        //MatchEntity -> chattingRoomIds
        ArrayList<Long> chattingRoomIds = chatConverter.toRoomIds(matchEntities);

        //토큰 발급
        String jwt = chatService.generateTokenWithMemberIdAndRoomIds(loginMemberEntity.getId(),chattingRoomIds);

        return jwt;
    }

    /**
     *  매주 화요일 00시  채팅방 일괄 자동닫음
     */
    @Transactional
    @Scheduled(cron = "0 0 0 ? * TUE")
    public void closeAllChattingRooms() {
        List<ChattingRoomEntity> chattingRoomEntities = chatService.findAllByStatus(ChattingRoomEntity.RoomStatus.OPEN);
        for(ChattingRoomEntity chattingRoomEntity : chattingRoomEntities){
            chattingRoomEntity.updateStatus(ChattingRoomEntity.RoomStatus.CLOSE);
        }
        List<MemberEntity> memberEntities = memberService.findAllAppliedMember();
        for(MemberEntity memberEntity : memberEntities){
            memberEntity.updateStatus(MemberEntity.Status.NOT_APPLIED);
        }
    }

    /**
     *  매주 목요일 23시 매칭 자동실행
     */
    @Transactional
    @Scheduled(cron = "0 0 23 ? * THU") // 매주 목요일 23시 매칭 진행
    public void matchingStart() throws Exception {
        matchBusiness.matchingAllApplicant();
    }

}
