package com.aliens.friendship.domain.article.comment.controller;

import com.aliens.friendship.domain.article.comment.dto.ArticleCommentsDto;
import com.aliens.friendship.domain.article.comment.dto.CreateArticleCommentRequest;
import com.aliens.friendship.domain.article.comment.dto.UpdateArticleCommentRequest;
import com.aliens.friendship.domain.article.comment.service.CommunityArticleCommentService;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommunityArticleCommentController {

    private final CommunityArticleCommentService communityArticleCommentService;

    @GetMapping("/api/v2/community-articles/{article-id}/comments")
    public ResponseEntity<ListResult<ArticleCommentsDto>> getAllCommunityArticleComments(
            @PathVariable("article-id") Long articleId
    ) {
        List<ArticleCommentsDto> allBoardArticleComment = communityArticleCommentService.getAllArticleComments(articleId);
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        allBoardArticleComment
                )
        );
    }

    @PostMapping("/api/v2/community-articles/{article-id}/comments")
    public ResponseEntity<CommonResult> createCommunityArticleComment(
            @PathVariable("article-id") Long articleId,
            @RequestBody CreateArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        communityArticleCommentService.createCommunityArticleComment(
                articleId,
                request,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 생성되었습니다."
                )
        );
    }

    @PostMapping("/api/v2/community-article-comments/{article-comment-id}/comments")
    public ResponseEntity<CommonResult> createCommunityArticleCommentReply(
            @PathVariable("article-comment-id") Long commentId,
            @RequestBody CreateArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        communityArticleCommentService.createCommunityArticleCommentReply(
                commentId,
                request,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 생성되었습니다."
                )
        );
    }

    @PatchMapping("/api/v2/community-article-comments/{article-comment-id}")
    public ResponseEntity<CommonResult> updateCommunityArticleComment(
            @PathVariable("article-comment-id") Long commentId,
            @RequestBody UpdateArticleCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        communityArticleCommentService.updateCommunityArticleComment(
                commentId,
                request,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 수정되었습니다."
                )
        );
    }

    @DeleteMapping("/api/v2/community-article-comments/{article-comment-id}")
    public ResponseEntity<CommonResult> deleteCommunityArticleComment(
            @PathVariable("article-comment-id") Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        communityArticleCommentService.deleteCommunityArticleComment(
                commentId,
                userPrincipal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }
}