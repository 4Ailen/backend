package com.aliens.db.productimage.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductImageEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticleEntity marketArticle;

    private ProductImageEntity(
            String imageUrl,
            MarketArticleEntity marketArticle
    ) {
        this.imageUrl = imageUrl;
        this.marketArticle = marketArticle;
    }

    public static ProductImageEntity of(
            String imageUrl,
            MarketArticleEntity marketArticle
    ) {
        return new ProductImageEntity(
                imageUrl,
                marketArticle
        );
    }
}