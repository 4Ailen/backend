package com.aliens.friendship.domain.article.controller;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.friendship.domain.article.dto.ArticleCategoryDto;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.domain.article.service.ArticleService;
import com.aliens.friendship.global.response.ListResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.aliens.db.communityarticle.ArticleCategory.MARKET;

@RequiredArgsConstructor
@Controller
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 게시글 카테고리 조회
     */
    @ResponseBody
    @GetMapping("/api/v2/article-categories")
    public ResponseEntity<SingleResult<ArticleCategoryDto>> getAllArticleCategories() {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회되었습니다.",
                        articleService.getAllArticleCategories()
                )
        );
    }

    /**
     * 전체 게시글 검색
     */
    @ResponseBody
    @GetMapping("/api/v2/articles")
    public ResponseEntity<ListResult<ArticleDto>> searchArticles(
            @RequestParam(name = "search-keyword", required = false) String searchKeyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ListResult.of(
                        "성공적으로 조회되었습니다.",
                        articleService.searchArticles(pageable, searchKeyword).getContent()
                )
        );
    }

    /**
     * 전체 게시글 상세 조회
     */
    @GetMapping("/api/v2/articles/{article-id}")
    public String getArticle(
            @PathVariable("article-id") Long articleId,
            @RequestParam("article-category") ArticleCategory articleCategory
    ) {
        if (Objects.equals(articleCategory, MARKET)) {
            return "forward:/api/v2/market-articles/" + articleId;
        }

        return "forward:/api/v2/community-articles/" + articleId;
    }
}