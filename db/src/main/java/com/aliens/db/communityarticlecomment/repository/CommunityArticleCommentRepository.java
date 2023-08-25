package com.aliens.db.communityarticlecomment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.article.comment.entity.CommunityArticleComment;
import site.packit.packit.domain.article.community.entity.CommunityArticle;

import java.util.List;

public interface CommunityArticleCommentRepository extends JpaRepository<CommunityArticleComment, Long> {
    List<CommunityArticleComment> findAllByCommunityArticle_Id(Long id);

    List<CommunityArticleComment> findAllByParentCommentId(Long id);

    int countAllByCommunityArticle(CommunityArticle communityArticle);
}