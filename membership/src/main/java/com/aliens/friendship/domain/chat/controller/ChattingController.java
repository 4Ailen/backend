package com.aliens.friendship.domain.chat.controller;

import com.aliens.friendship.domain.chat.business.ChatBusiness;
import com.aliens.friendship.domain.chat.service.ChatService;
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

    private final ChatService chatService;
    private final ChatBusiness chatBusiness;

    @GetMapping("/token")
    public ResponseEntity<SingleResult<String>> getJWTTokenForChatting() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 토큰이 발급되었습니다.",
                        chatBusiness.getJwtForChattingServer()
                )
        );
    }
}