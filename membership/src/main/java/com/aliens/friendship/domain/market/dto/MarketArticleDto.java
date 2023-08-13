package com.aliens.friendship.domain.market.dto;

import com.aliens.friendship.domain.market.constant.MarketArticleStatus;
import com.aliens.friendship.domain.market.constant.ProductStatus;
import com.aliens.friendship.domain.market.entity.MarketArticle;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MarketArticleDto {

    private Long marketArticleId;
    private String title;
    private MarketArticleStatus status;
    private Integer price;
    private ProductStatus productStatus;
    private String content;
    List<String> images;
    MemberDto member;

    private MarketArticleDto(
            Long marketArticleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> images,
            MemberDto member
    ) {
        this.marketArticleId = marketArticleId;
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.images = images;
        this.member = member;
    }

    public static MarketArticleDto of(
            Long marketArticleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> images,
            MemberDto member
    ) {
        return new MarketArticleDto(
                marketArticleId,
                title,
                status,
                price,
                productStatus,
                content,
                images,
                member
        );
    }

    public static MarketArticleDto from(
            MarketArticle marketArticle,
            List<String> images
    ) {
        return new MarketArticleDto(
                marketArticle.getId(),
                marketArticle.getTitle(),
                marketArticle.getStatus(),
                marketArticle.getPrice(),
                marketArticle.getProductStatus(),
                marketArticle.getContent(),
                images,
                MemberDto.from(marketArticle.getMember())
        );
    }
}