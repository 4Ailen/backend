package com.aliens.db.productimage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.global.audit.BaseEntity;

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
    private MarketArticle marketArticle;

    private ProductImageEntity(
            String imageUrl,
            MarketArticle marketArticle
    ) {
        this.imageUrl = imageUrl;
        this.marketArticle = marketArticle;
    }

    public static ProductImageEntity of(
            String imageUrl,
            MarketArticle marketArticle
    ) {
        return new ProductImageEntity(
                imageUrl,
                marketArticle
        );
    }
}