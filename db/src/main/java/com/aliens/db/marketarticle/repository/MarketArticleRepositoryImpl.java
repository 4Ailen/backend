package com.aliens.db.marketarticle.repository;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticlecomment.entity.QMarketArticleCommentEntity;
import com.aliens.db.marketbookmark.entity.QMarketBookmarkEntity;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.aliens.db.marketarticle.entity.QMarketArticleEntity.marketArticleEntity;
import static com.aliens.db.marketarticlecomment.entity.QMarketArticleCommentEntity.*;
import static com.aliens.db.marketbookmark.entity.QMarketBookmarkEntity.*;

@RequiredArgsConstructor
public class MarketArticleRepositoryImpl implements MarketArticleCustomRepository {
    private final EntityManager entityManager;

    @Override
    public Page<MarketArticleEntity> findAllWithFetchJoin(Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<MarketArticleEntity> result = queryFactory
                .selectFrom(marketArticleEntity)
                .leftJoin(marketArticleEntity.likes, marketBookmarkEntity)
                .leftJoin(marketArticleEntity.comments, marketArticleCommentEntity)
                .fetchJoin()
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Page<MarketArticleEntity> findAllByTitleContainingOrContentContainingByFetchJoin(String title, String content, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<MarketArticleEntity> result = queryFactory
                .selectFrom(marketArticleEntity)
                .leftJoin(marketArticleEntity.likes, marketBookmarkEntity)
                .leftJoin(marketArticleEntity.comments, marketArticleCommentEntity)
                .fetchJoin()
                .where(
                        marketArticleEntity.title.containsIgnoreCase(title)
                                .or(marketArticleEntity.content.containsIgnoreCase(content))
                )
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

}
