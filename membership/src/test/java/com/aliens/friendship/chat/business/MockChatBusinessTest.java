package com.aliens.friendship.chat.business;

import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.chat.business.ChatBusiness;
import com.aliens.friendship.domain.chat.converter.ChatConverter;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.match.service.MatchService;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MockChatBusinessTest {

    @Mock
    private ChatService chatService;

    @Mock
    private MatchService matchService;

    @Mock
    private ChatConverter chatConverter;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ChatBusiness chatBusiness;

    private MemberEntity loginMemberEntity;
    private MatchingEntity matchingEntity1;
    private MatchingEntity matchingEntity2;
    private List<MatchingEntity> matchEntities;

    //given method
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        loginMemberEntity = MemberEntity.builder().id(1L).build();

        matchingEntity1 = MatchingEntity.builder().id(100L).build();
        matchingEntity2 = MatchingEntity.builder().id(200L).build();

        matchEntities = List.of(matchingEntity1, matchingEntity2);
    }

    @Test
    @DisplayName("채팅 서버용 JWT 토큰 발급 - 성공")
    public void testGetJwtForChattingServer() throws Exception {
        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(matchService.findMatchEntityWithOpenedChattingRoomByMemberEntity(loginMemberEntity)).thenReturn(matchEntities);
        when(chatConverter.toRoomIds(matchEntities)).thenReturn(new ArrayList<>(List.of(100L, 200L)));
        when(chatService.generateTokenWithMemberIdAndRoomIds(loginMemberEntity.getId(), List.of(100L, 200L)))
                .thenReturn("test-jwt-token");

        // then
        String jwtToken = chatBusiness.getJwtForChattingServer();

        // Verify the result
        assertEquals("test-jwt-token", jwtToken);
    }
}
