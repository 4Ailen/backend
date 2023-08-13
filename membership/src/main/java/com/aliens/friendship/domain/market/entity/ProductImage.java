package com.aliens.friendship.domain.market.entity;

import com.aliens.db.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductImage
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticle marketArticle;

    private ProductImage(
            String imageUrl,
            MarketArticle marketArticle
    ) {
        this.imageUrl = imageUrl;
        this.marketArticle = marketArticle;
    }

    public static ProductImage of(
            String imageUrl,
            MarketArticle marketArticle
    ) {
        return new ProductImage(
                imageUrl,
                marketArticle
        );
    }
}