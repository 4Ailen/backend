package com.aliens.friendship.domain.chatting.controller;

import com.aliens.friendship.domain.auth.dto.RoomInfoDto;
import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChattingController {

    private final ChattingService chatService;

    @GetMapping("/room")
    public ResponseEntity<ListResult<RoomInfoDto>> getRooms() {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 방 정보를 전송합니다.",
                        chatService.getRoomInfoDtoListByMatchingCurrentMemberId()
                )
        );
    }

    @GetMapping("/token")
    public ResponseEntity<SingleResult<String>> getJWTTokenForChatting() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 토큰이 발급되었습니다.",
                        chatService.getJWTTokenForChatting()
                )
        );
    }
}