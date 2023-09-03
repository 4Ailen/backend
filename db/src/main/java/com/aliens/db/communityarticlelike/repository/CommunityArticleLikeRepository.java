package com.aliens.db.communityarticlelike.repository;

import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityArticleLikeRepository extends JpaRepository<CommunityArticleLikeEntity, Long> {

    List<CommunityArticleLikeEntity> findAllByCommunityArticle(CommunityArticleEntity communityArticle);

    void deleteAllByCommunityArticleAndMemberEntity(CommunityArticleEntity communityArticle, MemberEntity member);

    List<CommunityArticleLikeEntity> findAllByMemberEntity(MemberEntity member);

    Optional<CommunityArticleLikeEntity> findByCommunityArticleAndMemberEntity(CommunityArticleEntity communityArticle, MemberEntity member);
}