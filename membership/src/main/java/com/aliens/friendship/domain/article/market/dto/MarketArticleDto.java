package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MarketArticleDto {

    private Long articleId;
    private String title;
    private MarketArticleStatus status;
    private Integer price;
    private ProductStatus productStatus;
    private String content;
    private Integer marketArticleBookmarkCount;
    private Integer commentsCount;
    List<String> images;
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
            List<String> images,
            Instant createdAt,
            MemberDto member
    ) {
        this.articleId = articleId;
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.marketArticleBookmarkCount = marketArticleBookmarkCount;
        this.commentsCount = commentsCount;
        this.images = images;
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
            List<String> images,
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
                images,
                createdAt,
                member
        );
    }

    public static MarketArticleDto from(
            MarketArticleEntity marketArticle,
            Integer marketArticleBookmarkCount,
            Integer commentsCount,
            List<String> images
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
                images,
                marketArticle.getCreatedAt(),
                MemberDto.from(marketArticle.getMember())
        );
    }
}