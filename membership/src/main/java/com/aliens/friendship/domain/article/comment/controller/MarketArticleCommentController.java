package com.aliens.friendship.domain.article.comment.controller;

import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.friendship.domain.article.comment.dto.ArticleCommentsDto;
import com.aliens.friendship.domain.article.comment.dto.CreateArticleCommentRequest;
import com.aliens.friendship.domain.article.comment.dto.UpdateArticleCommentRequest;
import com.aliens.friendship.domain.article.comment.service.MarketArticleCommentService;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.fcm.service.FcmService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MarketArticleCommentController {

    private final MarketArticleCommentService marketArticleCommentService;
    private final FcmService fcmService;

    @GetMapping("/api/v2/market-articles/{article-id}/market-article-comments")
    public ResponseEntity<ListResult<ArticleCommentsDto>> getAllMarketArticleComment(
            @PathVariable("article-id") Long articleId
    ) {
        List<ArticleCommentsDto> allMarketArticleComment = marketArticleCommentService.getAllMarketArticleComments(articleId);
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        allMarketArticleComment
                )
        );
    }

    @PostMapping("/api/v2/market-articles/{article-id}/market-article-comments")
    public ResponseEntity<CommonResult> createMarketArticleComment(
            @PathVariable("article-id") Long marketArticleId,
            @RequestBody CreateArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws Exception {
        MarketArticleCommentEntity marketArticleComment = marketArticleCommentService.createMarketArticleComment(
                marketArticleId,
                request,
                userPrincipal
        );
        fcmService.sendArticleCommentNoticeToWriter(marketArticleComment);

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 생성되었습니다."
                )
        );
    }

    @PostMapping("/api/v2/market-articles/{article-id}/market-article-comments/{article-comment-id}")
    public ResponseEntity<CommonResult> createMarketArticleCommentReply(
            @PathVariable("article-id") Long marketArticleId,
            @PathVariable("article-comment-id") Long marketArticleCommentId,
            @RequestBody CreateArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws Exception {
        MarketArticleCommentEntity marketArticleComment = marketArticleCommentService.createMarketArticleCommentReply(
                marketArticleId,
                marketArticleCommentId,
                request,
                userPrincipal
        );
        fcmService.sendArticleCommentReplyNotice(marketArticleComment);

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 생성되었습니다."
                )
        );
    }

    @PatchMapping("/api/v2/market-article-comments/{article-comment-id}")
    public ResponseEntity<CommonResult> updateMarketArticleComment(
            @PathVariable("article-comment-id") Long marketArticleCommentId,
            @RequestBody UpdateArticleCommentRequest request,
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

    @DeleteMapping("/api/v2/market-article-comments/{article-comment-id}")
    public ResponseEntity<CommonResult> deleteMarketArticleComment(
            @PathVariable("article-comment-id") Long marketArticleCommentId,
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