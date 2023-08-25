package com.aliens.db.communityarticle.repository;

import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import site.packit.packit.domain.article.community.entity.CommunityArticle;
import site.packit.packit.domain.article.constant.ArticleCategory;
import site.packit.packit.domain.community.entity.QBoardArticle;

import java.util.List;

public interface CommunityArticleRepository
        extends JpaRepository<CommunityArticle, Long>,
        QuerydslPredicateExecutor<CommunityArticle>,
        QuerydslBinderCustomizer<QBoardArticle>
{
    Page<CommunityArticle> findAllByCategory(ArticleCategory category, Pageable pageable);
    List<CommunityArticle> findAllByTitleContainingOrContentContaining(String title, String content);
    Page<CommunityArticle> findAllByCategoryAndTitleContainingOrContentContaining(ArticleCategory category, String title, String content, Pageable pageable);

    @Override
    default void customize(
            QuerydslBindings bindings,
            QBoardArticle root
    ) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(
                root.title,
                root.content,
                root.category
        );
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
    }
}