package com.aliens.friendship.global.converter;

import com.aliens.db.communityarticle.ArticleCategory;
import org.springframework.core.convert.converter.Converter;

public class ArticleCategoryConverter implements Converter<String, ArticleCategory> {
    @Override
    public ArticleCategory convert(String category) {
        return ArticleCategory.of(category);
    }
}
