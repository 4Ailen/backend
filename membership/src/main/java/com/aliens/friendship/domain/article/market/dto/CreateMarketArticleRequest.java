package com.aliens.friendship.domain.article.market.dto;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class CreateMarketArticleRequest {

    private String title;
    private String marketArticleStatus;
    private Integer price;
    private String productStatus;
    private String content;
    private List<MultipartFile> imageUrls;

    public MarketArticleEntity toEntity(
            MemberEntity member
    ) {
        return MarketArticleEntity.of(
                title,
                MarketArticleStatus.of(marketArticleStatus),
                price,
                ProductStatus.of(productStatus),
                content,
                member
        );
    }
}