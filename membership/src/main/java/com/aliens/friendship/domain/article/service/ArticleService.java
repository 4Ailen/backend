package com.aliens.friendship.domain.article.service;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.repository.CommunityArticleRepository;
import com.aliens.db.marketarticle.repository.MarketArticleRepository;
import com.aliens.friendship.domain.article.community.service.CommunityArticleService;
import com.aliens.friendship.domain.article.dto.ArticleCategoryDto;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.domain.article.market.service.MarketArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final CommunityArticleRepository communityArticleRepository;
    private final CommunityArticleService communityArticleService;
    private final MarketArticleRepository marketArticleRepository;
    private final MarketArticleService marketArticleService;

    /**
     * 게시판 카테고리 전체 조회
     */
    public ArticleCategoryDto getAllArticleCategories() {
        return ArticleCategoryDto.of(
                Arrays.stream(ArticleCategory.values()).collect(Collectors.toList())
        );
    }

    /**
     * 전체 게시판 검색
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(
            Pageable pageable,
            String searchKeyword
    ) {
        List<ArticleDto> communityArticles = searchCommunityArticle(searchKeyword);
        List<ArticleDto> marketArticles = searchMarketArticle(searchKeyword);

        List<ArticleDto> results = new ArrayList<>();
        results.addAll(communityArticles);
        results.addAll(marketArticles);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }

    private List<ArticleDto> searchCommunityArticle(
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return communityArticleRepository.findAll()
                    .stream()
                    .map(article ->
                            ArticleDto.from(
                                    article,
                                    communityArticleService.getCommunityArticleLikesCount(article),
                                    communityArticleService.getCommunityArticleCommentCount(article),
                                    communityArticleService.getCommunityArticleImages(article)
                            )
                    ).collect(Collectors.toList());
        }

        return communityArticleRepository.findAllByTitleContainingOrContentContaining(
                        searchKeyword,
                        searchKeyword
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        communityArticleService.getCommunityArticleLikesCount(article),
                        communityArticleService.getCommunityArticleCommentCount(article),
                        communityArticleService.getCommunityArticleImages(article)
                ))
                .collect(Collectors.toList());
    }

    private List<ArticleDto> searchMarketArticle(
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return marketArticleRepository.findAll()
                    .stream()
                    .map(article ->
                            ArticleDto.from(
                                    article,
                                    marketArticleService.getMarketArticleBookmarkCount(article),
                                    marketArticleService.getMarketArticleCommentsCount(article),
                                    marketArticleService.getMarketArticleImages(article)
                            )
                    ).collect(Collectors.toList());
        }

        return marketArticleRepository.findAllByTitleContainingOrContentContaining(
                        searchKeyword,
                        searchKeyword
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        marketArticleService.getMarketArticleBookmarkCount(article),
                        marketArticleService.getMarketArticleCommentsCount(article),
                        marketArticleService.getMarketArticleImages(article)
                ))
                .collect(Collectors.toList());
    }
}