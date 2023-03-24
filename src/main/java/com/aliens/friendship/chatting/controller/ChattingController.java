package com.aliens.friendship.chatting.controller;

import com.aliens.friendship.jwt.domain.dto.RoomInfoDto;
import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.chatting.service.ChattingService;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chatService;
    private final MemberService memberService;

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @GetMapping("/chat/rooms")
    public Response<List<RoomInfoDto>> getRooms()throws Exception{
        String email = getCurrentMemberEmail();
        Member currentMember = memberService.findByEmail(email);
        List<RoomInfoDto> matchedRooms = chatService.getRoomInfoDtosByMatchingParticipantId(currentMember.getId());
        return Response.SUCCESS(matchedRooms);
    }

    // 사용하지 않기에 주석처리
//    @PatchMapping("/chat/room/{roomId}/status/{status}")
//    public Response<String> updateRoomStatus(@PathVariable int roomId, @PathVariable String status){
//        chatService.updateRoomStatus(roomId, status);
//        return Response.SUCCESS("Room status updated");
//    }
}
