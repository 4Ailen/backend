package com.aliens.db.marketarticlecomment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.market.entity.MarketArticle;
import site.packit.packit.domain.article.comment.entity.MarketArticleComment;
import site.packit.packit.domain.member.entity.MemberEntity;

import java.util.List;

public interface MarketArticleCommentRepository
        extends JpaRepository<MarketArticleComment, Long> {

    List<MarketArticleComment> findAllByMarketArticle_Id(Long marketArticleId);

    void deleteByMarketArticleAndMember(MarketArticle marketArticle, MemberEntity member);

    void deleteAllByMarketArticle(MarketArticle marketArticle);

    List<MarketArticleComment> findAllByParentCommentId(Long parentId);

    int countAllByMarketArticle(MarketArticle marketArticle);
}
