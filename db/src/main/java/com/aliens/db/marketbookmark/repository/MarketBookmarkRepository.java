package com.aliens.db.marketbookmark.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketbookmark.entity.MarketBookmarkEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketBookmarkRepository extends JpaRepository<MarketBookmarkEntity, Long> {

    void deleteAllByMarketArticleAndMemberEntity(MarketArticleEntity marketArticle, MemberEntity member);

    void deleteAllByMarketArticle(MarketArticleEntity marketArticle);

    List<MarketBookmarkEntity> findAllByMemberEntity(MemberEntity member);

    int countAllByMarketArticle(MarketArticleEntity marketArticle);
}