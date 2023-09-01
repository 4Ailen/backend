package com.aliens.friendship.domain.article.market.controller;

import com.aliens.friendship.domain.article.market.dto.CreateMarketArticleRequest;
import com.aliens.friendship.domain.article.market.dto.CreateMarketResponse;
import com.aliens.friendship.domain.article.market.dto.MarketArticleDto;
import com.aliens.friendship.domain.article.market.dto.UpdateMarketArticleRequest;
import com.aliens.friendship.domain.article.market.service.MarketArticleService;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
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

@RequiredArgsConstructor
@RequestMapping("/api/v2/market-articles")
@RestController
public class MarketArticleController {

    private final MarketArticleService marketArticleService;

    /**
     * 장터 게시판 검색
     */
    @GetMapping
    public ResponseEntity<ListResult<MarketArticleDto>> searchMarketArticles(
            @RequestParam(name = "search-keyword", required = false) String searchKeyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        marketArticleService.searchMarketArticles(
                                pageable,
                                searchKeyword
                        ).getContent()
                )
        );
    }

    /**
     * 장터 게시글 상세 조회
     */
    @GetMapping("/{article-id}")
    public ResponseEntity<SingleResult<MarketArticleDto>> getMarketArticle(
            @PathVariable("article-id") Long articleId
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회되었습니다.",
                        marketArticleService.getMarketArticle(articleId)
                )
        );
    }

    /**
     * 장터 게시글 생성
     */
    @PostMapping
    public ResponseEntity<SingleResult<CreateMarketResponse>> createMarketArticle(
            @RequestBody CreateMarketArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 생성되었습니다.",
                        new CreateMarketResponse(
                                marketArticleService.saveMarketArticle(
                                        request,
                                        principal
                                )
                        )
                )
        );
    }

    /**
     * 장터 게시글 수정
     */
    @PatchMapping("/{article-id}")
    public ResponseEntity<CommonResult> updateMarketArticle(
            @PathVariable("article-id") Long articleId,
            @RequestBody UpdateMarketArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketArticleService.updateMarketArticle(
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

    /**
     * 장터 게시글 삭제
     */
    @DeleteMapping("/{article-id}")
    public ResponseEntity<CommonResult> deleteMarketArticle(
            @PathVariable("article-id") Long articleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketArticleService.deleteMarketArticle(
                articleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }

    @PostMapping("/{article-id}/bookmarks")
    public ResponseEntity<CommonResult> createBookmark(
            @PathVariable("article-id") Long articleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketArticleService.createBookmark(
                articleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 북마크가 등록되었습니다."
                )
        );
    }

    @DeleteMapping("/{article-id}/bookmarks")
    public ResponseEntity<CommonResult> deleteBookmark(
            @PathVariable("article-id") Long articleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketArticleService.deleteBookmark(
                articleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 북마크가 해제되었습니다."
                )
        );
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<ListResult<MarketArticleDto>> getAllBookmarks(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        marketArticleService.getAllBookmarks(
                                principal
                        )
                )
        );
    }
}