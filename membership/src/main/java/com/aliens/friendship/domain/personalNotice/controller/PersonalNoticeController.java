package com.aliens.friendship.domain.personalNotice.controller;

import com.aliens.friendship.domain.personalNotice.dto.PersonalNoticesDto;
import com.aliens.friendship.domain.personalNotice.service.PersonalNoticeService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/board")
public class PersonalNoticeController {

    private final PersonalNoticeService personalNoticeService;

    @PutMapping("/personal-notice/read/{personal-notice-id}")
    public ResponseEntity<CommonResult> markAsRead(@PathVariable("personal-notice-id") Long personalNoticeId) {
        personalNoticeService.markAsRead(personalNoticeId);
        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 알림 읽음 처리되었습니다."
                )
        );
    }

}