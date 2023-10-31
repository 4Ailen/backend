package com.aliens.db.marketarticle.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface MarketArticleCustomRepository {

    Page<MarketArticleEntity> findAllWithFetchJoin(Pageable pageable);

    Page<MarketArticleEntity> findAllByTitleContainingOrContentContainingByFetchJoin(String title, String content, Pageable pageable);

    Optional<Instant> findLatestArticleTimeByMemberId(Long memberId);
}
