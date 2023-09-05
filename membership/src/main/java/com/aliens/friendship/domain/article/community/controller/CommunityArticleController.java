package com.aliens.friendship.domain.article.community.controller;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.friendship.domain.article.community.dto.CreateCommunityArticleRequest;
import com.aliens.friendship.domain.article.community.dto.CreateCommunityArticleResponse;
import com.aliens.friendship.domain.article.community.dto.UpdateCommunityArticleRequest;
import com.aliens.friendship.domain.article.community.dto.UpdateLikeResponse;
import com.aliens.friendship.domain.article.community.service.CommunityArticleService;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.fcm.service.FcmService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class CommunityArticleController {

    private final CommunityArticleService communityArticleService;
    private final FcmService fcmService;

    /**
     * 커뮤니티 게시글 검색
     */
    @GetMapping("/api/v2/community-articles")
    public ResponseEntity<ListResult<ArticleDto>> searchCommunityArticles(
            @RequestParam ArticleCategory category,
            @RequestParam(name = "search-keyword", required = false) String searchKeyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        communityArticleService.searchCommunityArticles(
                                pageable,
                                category,
                                searchKeyword
                        ).getContent()
                )
        );
    }

    /**
     * 커뮤니티 게시글 상세 조회
     */
    @GetMapping("/api/v2/community-articles/{article-id}")
    public ResponseEntity<SingleResult<ArticleDto>> getCommunityArticle(
            @PathVariable("article-id") Long articleId
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회되었습니다.",
                        communityArticleService.getCommunityArticle(articleId)
                )
        );
    }

    /**
     * 커뮤니티 게시글 생성
     */
    @PostMapping("/api/v2/community-articles")
    public ResponseEntity<SingleResult<CreateCommunityArticleResponse>> saveCommunityArticle(
            @RequestBody CreateCommunityArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 생성되었습니다.",
                                new CreateCommunityArticleResponse(
                                    communityArticleService.saveCommunityArticle(
                                            request,
                                            principal
                                    )
                        )
                )
        );
    }

    /**
     * 커뮤니티 게시글 수정
     */
    @PatchMapping("/api/v2/community-articles/{article-id}")
    public ResponseEntity<CommonResult> updateCommunityArticle(
            @PathVariable("article-id") Long articleId,
            @RequestBody UpdateCommunityArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        communityArticleService.updateCommunityArticle(
                articleId,
                request,
                principal
        );
        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 수정되었습니다."
                )
        );
    }

    @DeleteMapping("/api/v2/community-articles/{article-id}")
    public ResponseEntity<CommonResult> deleteCommunityArticle(
            @PathVariable("article-id") Long articleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        communityArticleService.deleteCommunityArticle(
                articleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }

    @PostMapping("/api/v2/community-articles/{article-id}/likes")
    public ResponseEntity<SingleResult<UpdateLikeResponse>> createCommunityArticleLike(
            @PathVariable("article-id") Long articleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws Exception {
        Optional<CommunityArticleLikeEntity> communityArticleLike = communityArticleService.updateArticleLike(articleId, principal);
        UpdateLikeResponse updateLikedResponse;
        if(communityArticleLike.isPresent()){
            fcmService.sendArticleLikeNoticeToWriter(communityArticleLike.get());
            updateLikedResponse = new UpdateLikeResponse(communityArticleService.getCommunityArticleLikesCount(articleId), true);
        } else{
            updateLikedResponse = new UpdateLikeResponse(communityArticleService.getCommunityArticleLikesCount(articleId), false);
        }

        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 좋아요가 처리되었습니다.",
                        updateLikedResponse
                )
        );
    }

//    @GetMapping("/api/v2/community-articles/likes")
//    public ResponseEntity<ListResult<ArticleDto>> getAllLikes(
//    ) {
//        return ResponseEntity.ok(
//                ListResult.of(
//                        "성공적으로 조회되었습니다.",
//                        communityArticleService.getAllLikes()
//                )
//        );
//    }
}