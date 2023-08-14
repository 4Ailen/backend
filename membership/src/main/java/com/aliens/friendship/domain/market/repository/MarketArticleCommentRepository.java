package com.aliens.friendship.domain.market.repository;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.market.entity.MarketArticle;
import com.aliens.friendship.domain.market.entity.MarketArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketArticleCommentRepository
        extends JpaRepository<MarketArticleComment, Long> {

    List<MarketArticleComment> findAllByMarketArticle_Id(Long marketArticleId);

    void deleteByMarketArticleAndMember(MarketArticle marketArticle, MemberEntity member);
}
