package com.aliens.friendship.domain.market.controller;

import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.market.dto.MarketArticleCommentDto;
import com.aliens.friendship.domain.market.dto.request.CreateMarketArticleCommentRequest;
import com.aliens.friendship.domain.market.dto.request.UpdateMarketArticleCommentRequest;
import com.aliens.friendship.domain.market.service.MarketArticleCommentService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MarketArticleCommentController {

    private final MarketArticleCommentService marketArticleCommentService;

    @GetMapping("/api/v2/market-articles/{market-article-id}/market-article-comments")
    public ResponseEntity<ListResult<MarketArticleCommentDto>> getAllMarketArticleComment(
            @PathVariable("market-article-id") Long marketArticleId
    ) {
        List<MarketArticleCommentDto> allMarketArticleComment = marketArticleCommentService.getAllMarketArticleComment(marketArticleId);
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        allMarketArticleComment
                )
        );
    }

    @PostMapping("/api/v2/market-articles/{market-article-id}/market-article-comments")
    public ResponseEntity<CommonResult> createMarketArticleComment(
            @PathVariable("market-article-id") Long marketArticleId,
            @RequestBody CreateMarketArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        marketArticleCommentService.createMarketArticleComment(
                marketArticleId,
                request,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 생성되었습니다."
                )
        );
    }

    @PatchMapping("/api/v2/market-article-comments/{market-article-comment-id}")
    public ResponseEntity<CommonResult> updateMarketArticleComment(
            @PathVariable("market-article-comment-id") Long marketArticleCommentId,
            @RequestBody UpdateMarketArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        marketArticleCommentService.updateMarketArticleComment(
                marketArticleCommentId,
                request,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 수정되었습니다."
                )
        );
    }

    @DeleteMapping("/api/v2/market-article-comments/{market-article-comment-id}")
    public ResponseEntity<CommonResult> deleteMarketArticleComment(
            @PathVariable("market-article-comment-id") Long marketArticleCommentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        marketArticleCommentService.deleteMarketArticleComment(
                marketArticleCommentId,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }
}