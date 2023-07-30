package com.aliens.friendship.domain.block.business;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.block.service.BlockService;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Business
@Slf4j
public class BlockBusiness {

    private final MemberService memberService;
    private final ChatService chatService;
    private final BlockService blockService;
    /**
     * 회원간 차단
     */
    public void block(Long memberId, Long roomId) throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 차단할 회원 엔티티
        MemberEntity blockedMemberEntity = memberService.findById(memberId);

        //차단 저장
        blockService.block(blockedMemberEntity,loginMemberEntity);

        //채팅방 엔티티
        ChattingRoomEntity chattingRoomEntity = chatService.findChattingRoomById(roomId);

        //채팅방 닫음
        chatService.closeChattingRoom(chattingRoomEntity);
    }

}
