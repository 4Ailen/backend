package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CreateMarketArticleRequest {

    private String title;
    private String status;
    private Integer price;
    private String productStatus;
    private String content;
    private List<String> imageUrls;

    private CreateMarketArticleRequest(
            String title,
            String status,
            Integer price,
            String productStatus,
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
            String status,
            Integer price,
            String productStatus,
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
                MarketArticleStatus.of(status),
                price,
                ProductStatus.of(productStatus),
                content,
                member
        );
    }
}