package com.aliens.friendship.block.business;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.block.business.BlockBusiness;
import com.aliens.friendship.domain.block.service.BlockService;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MockBlockBusinessTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ChatService chatService;

    @Mock
    private BlockService blockService;

    @InjectMocks
    private BlockBusiness blockBusiness;

    private MemberEntity loginMemberEntity;
    private MemberEntity blockedMemberEntity;
    private ChattingRoomEntity chattingRoomEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginMemberEntity = MemberEntity.builder().build();
        blockedMemberEntity = MemberEntity.builder().build();
        chattingRoomEntity = new ChattingRoomEntity();
    }

    @Test
    @DisplayName("회원간 차단 - 성공")
    public void testBlock() throws Exception {
        // given
        Long memberId = 1L;
        Long roomId = 2L;

        //when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(memberService.findById(anyLong())).thenReturn(blockedMemberEntity);
        when(chatService.findChattingRoomById(anyLong())).thenReturn(chattingRoomEntity);

        // then
        blockBusiness.block(memberId, roomId);

        verify(blockService, times(1)).block(blockedMemberEntity, loginMemberEntity);
        verify(chatService, times(1)).closeChattingRoom(chattingRoomEntity);
    }
}