package com.aliens.friendship.domain.market.controller;

import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.market.dto.request.CreateMarketArticleRequest;
import com.aliens.friendship.domain.market.dto.MarketArticleDto;
import com.aliens.friendship.domain.market.dto.request.UpdateMarketArticleRequest;
import com.aliens.friendship.domain.market.dto.response.CreateMarketResponse;
import com.aliens.friendship.domain.market.service.MarketService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v2/market-articles")
@RestController
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/")
    public ResponseEntity<ListResult<MarketArticleDto>> getMarketArticles() {
        List<MarketArticleDto> allMarketArticles = marketService.getAllMarketArticles();

        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        allMarketArticles
                )
        );
    }

    @GetMapping("/{market-article-id}")
    public ResponseEntity<SingleResult<MarketArticleDto>> getMarketArticle(
            @PathVariable("market-article-id") Long marketArticleId
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회되었습니다.",
                        marketService.getMarketArticle(marketArticleId)
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<SingleResult<CreateMarketResponse>> createMarketArticle(
            @RequestBody CreateMarketArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 생성되었습니다.",
                        new CreateMarketResponse(
                                marketService.saveMarketArticle(
                                        request,
                                        principal
                                )
                        )
                )
        );
    }

    @PatchMapping("/{market-article-id}")
    public ResponseEntity<CommonResult> updateMarketArticle(
            @PathVariable("market-article-id") Long marketArticleId,
            @RequestBody UpdateMarketArticleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketService.updateMarketArticle(
                marketArticleId,
                request,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 수정되었습니다."
                )
        );
    }

    @DeleteMapping("/{market-article-id}")
    public ResponseEntity<CommonResult> deleteMarketArticle(
            @PathVariable("market-article-id") Long marketArticleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketService.deleteMarketArticle(
                marketArticleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 삭제되었습니다."
                )
        );
    }

    @PostMapping("/{market-article-id}/bookmark")
    public ResponseEntity<CommonResult> createBookmark(
            @PathVariable("market-article-id") Long marketArticleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketService.createBookmark(
                marketArticleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 북마크가 등록되었습니다."
                )
        );
    }

    @DeleteMapping("/{market-article-id}/bookmark")
    public ResponseEntity<CommonResult> deleteBookmark(
            @PathVariable("market-article-id") Long marketArticleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        marketService.deleteBookmark(
                marketArticleId,
                principal
        );

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 북마크가 해제되었습니다."
                )
        );
    }
}