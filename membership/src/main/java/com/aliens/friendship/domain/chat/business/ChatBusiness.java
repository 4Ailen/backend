package com.aliens.friendship.domain.chat.business;

import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.chat.converter.ChatConverter;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.match.service.MatchService;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Business
@RequiredArgsConstructor
public class ChatBusiness {

    private final ChatService chatService;
    private final MatchService matchService;
    private final ChatConverter chatConverter;
    private final MemberService memberService;

    public String getJwtForChattingServer() throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 매칭 엔티티 리스트
        List<MatchingEntity> matchEntities = matchService.findByMemberEntity(loginMemberEntity);

        //MatchEntity -> chattingRoomIds
        ArrayList<Long> chattingRoomIds = chatConverter.toRoomIds(matchEntities);

        //토큰 발급
        String jwt = chatService.generateTokenWithMemberIdAndRoomIds(loginMemberEntity.getId(),chattingRoomIds);

        return jwt;
    }

}
