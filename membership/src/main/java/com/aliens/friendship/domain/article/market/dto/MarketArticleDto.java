package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class MarketArticleDto {

    private Long articleId;
    private String title;
    private String marketArticleStatus;
    private Integer price;
    private String productStatus;
    private String content;
    private Integer marketArticleBookmarkCount;
    private Integer commentsCount;
    List<String> imageUrls;
    private Instant createdAt;
    MemberDto member;

    private MarketArticleDto(
            Long articleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            Integer marketArticleBookmarkCount,
            Integer commentsCount,
            List<String> imageUrls,
            Instant createdAt,
            MemberDto member
    ) {
        this.articleId = articleId;
        this.title = title;
        this.marketArticleStatus = status.getValue();
        this.price = price;
        this.productStatus = productStatus.getValue();
        this.content = content;
        this.marketArticleBookmarkCount = marketArticleBookmarkCount;
        this.commentsCount = commentsCount;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
        this.member = member;
    }

    public static MarketArticleDto of(
            Long marketArticleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            Integer marketArticleBookmarkCount,
            Integer commentsCount,
            List<String> imageUrls,
            Instant createdAt,
            MemberDto member
    ) {
        return new MarketArticleDto(
                marketArticleId,
                title,
                status,
                price,
                productStatus,
                content,
                marketArticleBookmarkCount,
                commentsCount,
                imageUrls,
                createdAt,
                member
        );
    }

    public static MarketArticleDto from(
            MarketArticleEntity marketArticle,
            Integer marketArticleBookmarkCount,
            Integer commentsCount,
            List<String> imageUrls
    ) {
        return new MarketArticleDto(
                marketArticle.getId(),
                marketArticle.getTitle(),
                marketArticle.getStatus(),
                marketArticle.getPrice(),
                marketArticle.getProductStatus(),
                marketArticle.getContent(),
                marketArticleBookmarkCount,
                commentsCount,
                imageUrls,
                marketArticle.getCreatedAt(),
                MemberDto.from(marketArticle.getMember())
        );
    }
}