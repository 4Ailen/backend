package com.aliens.db.productimage.repsitory;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.productimage.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    void deleteAllByMarketArticle(MarketArticleEntity marketArticle);

    List<ProductImageEntity> findAllByMarketArticle(MarketArticleEntity marketArticle);
}