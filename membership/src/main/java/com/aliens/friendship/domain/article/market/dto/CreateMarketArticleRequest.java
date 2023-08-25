package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateMarketArticleRequest {

    private String title;
    private MarketArticleStatus status;
    private Integer price;
    private ProductStatus productStatus;
    private String content;
    private List<String> imageUrls;

    private CreateMarketArticleRequest(
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

    public static CreateMarketArticleRequest of(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            List<String> imageUrls
    ) {
        return new CreateMarketArticleRequest(
                title,
                status,
                price,
                productStatus,
                content,
                imageUrls
        );
    }

    public MarketArticleEntity toEntity(
            MemberEntity member
    ) {
        return MarketArticleEntity.of(
                title,
                status,
                price,
                productStatus,
                content,
                member
        );
    }
}