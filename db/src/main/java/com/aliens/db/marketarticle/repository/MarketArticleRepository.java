package com.aliens.db.marketarticle.repository;

import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import site.packit.packit.domain.article.market.entity.MarketArticle;
import site.packit.packit.domain.article.market.entity.QMarketArticle;

import java.util.List;

public interface MarketArticleRepository extends
        JpaRepository<MarketArticle, Long>,
        QuerydslPredicateExecutor<MarketArticle>,
        QuerydslBinderCustomizer<QMarketArticle>
{
    List<MarketArticle> findAllByTitleContainingOrContentContaining(String title, String content);
    Page<MarketArticle> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Override
    default void customize(
            QuerydslBindings bindings,
            QMarketArticle root
    ) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(
                root.title,
                root.content,
                root.productStatus,
                root.price,
                root.status
        );
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
    }
}