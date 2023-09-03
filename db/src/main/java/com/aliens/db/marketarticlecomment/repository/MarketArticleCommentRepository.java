package com.aliens.db.marketarticlecomment.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketArticleCommentRepository
        extends JpaRepository<MarketArticleCommentEntity, Long> {

    List<MarketArticleCommentEntity> findAllByMarketArticle_Id(Long marketArticleId);

    void deleteAllByMarketArticle(MarketArticleEntity marketArticle);

    List<MarketArticleCommentEntity> findAllByParentCommentId(Long parentId);

    int countAllByMarketArticle(MarketArticleEntity marketArticle);

    List<MarketArticleCommentEntity> findAllByMember(MemberEntity loginMemberEntity);
}
