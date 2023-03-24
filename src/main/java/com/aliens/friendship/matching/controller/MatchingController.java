package com.aliens.friendship.matching.controller;

import com.aliens.friendship.chatting.service.ChattingService;
import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.matching.service.MatchingInfoService;
import com.aliens.friendship.matching.controller.dto.MatchingParticipantInfo;
import com.aliens.friendship.matching.service.BlockingInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingInfoService matchingInfoService;
    private final BlockingInfoService blockingInfoService;
    private final ChattingService chattingService;


    @GetMapping("/matching/languages")
    public Response<Map<String, Object>> getLanguages() {
        return Response.SUCCESS(matchingInfoService.getLanguages());
    }


    @PostMapping("/matching/applicant")
    public void applyMatching(@RequestBody MatchingParticipantInfo matchingParticipant) {
        matchingInfoService.applyMatching(matchingParticipant);
    }

    @GetMapping("/matching/status")
    public Response<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", matchingInfoService.checkStatus());

        return Response.SUCCESS(status);
    }


    @GetMapping("/matching/partner/{memberId}/block/{roomId}")
    public Response<String> blocking(@PathVariable int memberId,Long roomId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        blockingInfoService.block(email,memberId);
        chattingService.saveChatMessage(roomId,"공지","차단된 상대입니다.",3);
        chattingService.updateRoomStatus(roomId, "CLOSE");
        return Response.SUCCESS("차단 성공");
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...
}
