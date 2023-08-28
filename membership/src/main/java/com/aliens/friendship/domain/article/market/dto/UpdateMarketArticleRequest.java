package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateMarketArticleRequest {

    private String title;
    private MarketArticleStatus status;
    private Integer price;
    private ProductStatus productStatus;
    private String content;
    private List<String> imageUrls;

    private UpdateMarketArticleRequest(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> imageUrls
    ) {
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static UpdateMarketArticleRequest of(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> imageUrls
    ) {
        return new UpdateMarketArticleRequest(
                title,
                status,
                price,
                productStatus,
                content,
                imageUrls
        );
    }
}