package com.aliens.friendship.domain.market.dto.request;

import com.aliens.friendship.domain.market.constant.MarketArticleStatus;
import com.aliens.friendship.domain.market.constant.ProductStatus;
import com.aliens.friendship.domain.market.entity.MarketArticle;
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

    private MarketArticleDto(
            Long marketArticleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> images
    ) {
        this.marketArticleId = marketArticleId;
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.images = images;
    }

    public static MarketArticleDto of(
            Long marketArticleId,
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> images
    ) {
        return new MarketArticleDto(
                marketArticleId,
                title,
                status,
                price,
                productStatus,
                content,
                images
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
                images
        );
    }
}