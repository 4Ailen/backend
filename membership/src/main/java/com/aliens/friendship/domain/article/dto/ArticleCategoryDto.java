package com.aliens.friendship.domain.article.dto;

import com.aliens.db.communityarticle.ArticleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ArticleCategoryDto {

    private List<String> categories;

    public static ArticleCategoryDto of(List<ArticleCategory> categories) {
        return new ArticleCategoryDto(
             categories
                     .stream()
                     .map(ArticleCategory::getValue)
                     .collect(Collectors.toList())
        );
    }
}