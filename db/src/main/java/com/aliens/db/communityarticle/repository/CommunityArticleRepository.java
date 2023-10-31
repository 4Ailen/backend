package com.aliens.db.communityarticle.repository;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommunityArticleRepository
        extends JpaRepository<CommunityArticleEntity, Long>, CommunityArticleCustomRepository {
    Page<CommunityArticleEntity> findAllByCategory(ArticleCategory category, Pageable pageable);

    List<CommunityArticleEntity> findAllByTitleContainingOrContentContaining(String title, String content);

    List<CommunityArticleEntity> findAllByMember(MemberEntity member);

    Page<CommunityArticleEntity> findAllByCategoryAndTitleContainingOrContentContaining(ArticleCategory category, String title, String content, Pageable pageable);

    Optional<Instant> findLatestArticleTimeByMemberId(Long memberId);

}