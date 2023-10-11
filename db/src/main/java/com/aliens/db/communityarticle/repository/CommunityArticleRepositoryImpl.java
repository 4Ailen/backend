package com.aliens.db.communityarticle.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;

import static com.aliens.db.communityarticle.entity.QCommunityArticleEntity.communityArticleEntity;

@RequiredArgsConstructor

public class CommunityArticleRepositoryImpl implements CommunityArticleCustomRepository {
    private final EntityManager entityManager;

    @Override
    public Optional<Instant> findLatestArticleTimeByMemberId(Long memberId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        return Optional.ofNullable(queryFactory
                .select(communityArticleEntity.createdAt.max())
                .from(communityArticleEntity)
                .where(communityArticleEntity.member.id.eq(memberId))
                .fetchOne());
    }
}
