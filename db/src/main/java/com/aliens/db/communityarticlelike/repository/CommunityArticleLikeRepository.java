package com.aliens.db.communityarticlelike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.community.entity.CommunityArticleLike;
import site.packit.packit.domain.article.community.entity.CommunityArticle;
import site.packit.packit.domain.member.entity.MemberEntity;

import java.util.List;

public interface CommunityArticleLikeRepository extends JpaRepository<CommunityArticleLike, Long> {

    List<CommunityArticleLike> findAllByCommunityArticle(CommunityArticle communityArticle);

    void deleteAllByCommunityArticleAndMemberEntity(CommunityArticle communityArticle, MemberEntity member);

    List<CommunityArticleLike> findAllByMemberEntity(MemberEntity member);
}