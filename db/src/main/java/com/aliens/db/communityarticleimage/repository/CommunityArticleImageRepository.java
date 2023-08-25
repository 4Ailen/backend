package com.aliens.db.communityarticleimage.repository;

import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticleimage.entity.CommunityArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityArticleImageRepository extends JpaRepository<CommunityArticleImageEntity, Long> {

    void deleteAllByCommunityArticle(CommunityArticleEntity communityArticle);

    List<CommunityArticleImageEntity> findAllByCommunityArticle(CommunityArticleEntity communityArticle);
}
