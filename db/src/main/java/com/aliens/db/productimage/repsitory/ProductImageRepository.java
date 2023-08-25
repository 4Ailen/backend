package com.aliens.db.productimage.repsitory;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.market.entity.MarketArticle;
import site.packit.packit.domain.article.market.entity.ProductImage;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    void deleteAllByMarketArticle(MarketArticle marketArticle);

    List<ProductImage> findAllByMarketArticle(MarketArticle marketArticle);
}