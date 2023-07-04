package com.aliens.friendship.domain.chatting.controller;

import com.aliens.friendship.domain.auth.dto.RoomInfoDto;
import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChattingController {

    private final ChattingService chatService;
    private final MemberService memberService;
    private final ResponseService responseService;

    @GetMapping("/room")
    public ListResult<RoomInfoDto> getRooms() {
        return responseService.getListResult(
                OK.value(),
                "성공적으로 방 정보를 전송합니다.",
                chatService.getRoomInfoDtoListByMatchingCurrentMemberId()
        );
    }

    @GetMapping("/token")
    public SingleResult<String> getJWTTokenForChatting() throws Exception {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 토큰이 발급되었습니다.",
                chatService.getJWTTokenForChatting()
        );
    }
}
