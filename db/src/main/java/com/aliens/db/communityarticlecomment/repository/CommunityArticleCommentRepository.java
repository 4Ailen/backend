package com.aliens.db.communityarticlecomment.repository;

import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityArticleCommentRepository extends JpaRepository<CommunityArticleCommentEntity, Long> {
    List<CommunityArticleCommentEntity> findAllByCommunityArticle_Id(Long id);

    List<CommunityArticleCommentEntity> findAllByParentCommentId(Long id);

    List<CommunityArticleCommentEntity> findAllByMember(MemberEntity member);


    int countAllByCommunityArticle(CommunityArticleEntity communityArticle);
}