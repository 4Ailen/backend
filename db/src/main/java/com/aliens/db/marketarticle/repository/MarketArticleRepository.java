package com.aliens.db.marketarticle.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketArticleRepository extends
        JpaRepository<MarketArticleEntity, Long> {
    List<MarketArticleEntity> findAllByTitleContainingOrContentContaining(String title, String content);

    Page<MarketArticleEntity> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}