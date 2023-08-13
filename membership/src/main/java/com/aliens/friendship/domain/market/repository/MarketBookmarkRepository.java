package com.aliens.friendship.domain.market.repository;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.market.entity.MarketArticle;
import com.aliens.friendship.domain.market.entity.MarketBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketBookmarkRepository extends JpaRepository<MarketBookmark, Long> {

    void deleteAllByMarketArticleAndMemberEntity(MarketArticle marketArticle, MemberEntity member);
}
