package com.aliens.db.marketbookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.market.entity.MarketArticle;
import site.packit.packit.domain.article.market.entity.MarketBookmark;
import site.packit.packit.domain.member.entity.MemberEntity;

import java.util.List;

public interface MarketBookmarkRepository extends JpaRepository<MarketBookmark, Long> {

    void deleteAllByMarketArticleAndMemberEntity(MarketArticle marketArticle, MemberEntity member);

    void deleteAllByMarketArticle(MarketArticle marketArticle);

    List<MarketBookmark> findAllByMemberEntity(MemberEntity member);

    int countAllByMarketArticle(MarketArticle marketArticle);
}
