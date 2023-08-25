package com.aliens.db.communityarticleimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.community.entity.CommunityArticle;
import site.packit.packit.domain.article.community.entity.CommunityArticleImage;

import java.util.List;

public interface CommunityArticleImageRepository extends JpaRepository<CommunityArticleImage, Long> {

    void deleteAllByCommunityArticle(CommunityArticle communityArticle);

    List<CommunityArticleImage> findAllByCommunityArticle(CommunityArticle communityArticle);
}
