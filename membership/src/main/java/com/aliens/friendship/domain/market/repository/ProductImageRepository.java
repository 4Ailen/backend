package com.aliens.friendship.domain.market.repository;

import com.aliens.friendship.domain.market.entity.MarketArticle;
import com.aliens.friendship.domain.market.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    void deleteAllByMarketArticle(MarketArticle marketArticle);

    List<ProductImage> findAllByMarketArticle(MarketArticle marketArticle);

}
