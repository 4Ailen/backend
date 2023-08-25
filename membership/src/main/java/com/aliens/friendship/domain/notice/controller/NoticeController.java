package com.aliens.friendship.domain.notice.controller;

import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.notice.dto.NoticeDto;
import com.aliens.friendship.domain.notice.dto.request.CreateNoticeRequest;
import com.aliens.friendship.domain.notice.dto.request.UpdateNoticeRequest;
import com.aliens.friendship.domain.notice.dto.response.CreateNoticeResponse;
import com.aliens.friendship.domain.notice.service.NoticeService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/api/v2/notices")
    public ResponseEntity<ListResult<NoticeDto>> getAllNotices() {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        noticeService.getAllNotices()
                )
        );
    }

    @GetMapping("/api/v2/notices/{notice-id}")
    public ResponseEntity<SingleResult<NoticeDto>> getNotice(
            @PathVariable("notice-id") Long noticeId
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회되었습니다.",
                        noticeService.getNotice(noticeId)
                )
        );
    }

    @PostMapping("/api/v2/notices")
    public ResponseEntity<SingleResult<CreateNoticeResponse>> createNotice(
            @RequestBody CreateNoticeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 생성되었습니다.",
                        new CreateNoticeResponse(
                                noticeService.createNotice(request, principal)
                        )
                )
        );
    }

    @PatchMapping("/api/v2/notices/{notice-id}")
    public ResponseEntity<CommonResult> updateNotice(
            @PathVariable("notice-id") Long noticeId,
            @RequestBody UpdateNoticeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        noticeService.updateNotice(
                noticeId,
                request,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 수정되었습니다."
                )
        );
    }

    @DeleteMapping("/api/v2/notices/{notice-id}")
    public ResponseEntity<CommonResult> deleteNotice(
            @PathVariable("notice-id") Long noticeId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        noticeService.deleteNotice(noticeId, principal);

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }
}