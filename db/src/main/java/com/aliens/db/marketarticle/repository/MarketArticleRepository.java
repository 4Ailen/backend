package com.aliens.db.marketarticle.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketArticleRepository extends
        JpaRepository<MarketArticleEntity, Long> {
    List<MarketArticleEntity> findAllByTitleContainingOrContentContaining(String title, String content);
    List<MarketArticleEntity> findAllByMember(MemberEntity member);
    Page<MarketArticleEntity> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}