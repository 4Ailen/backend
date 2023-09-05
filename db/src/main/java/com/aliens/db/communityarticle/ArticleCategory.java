package com.aliens.db.communityarticle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ArticleCategory {
    ALL("전체게시판"),
    MARKET("장터게시판"),
    FREE("자유게시판"),
    INFO("정보게시판"),
    MUSIC("음악게시판"),
    GAME("게임게시판"),
    FOOD("음식게시판"),
    FASHION("패션게시판");

    public String getValue() {
        return value;
    }

    private final String value;

    public static ArticleCategory of(String value) {
        if (value == null) {
            return ALL;
        }

        for (ArticleCategory category : ArticleCategory.values()) {
            if (category.value.equals(value)) {
                return category;
            }
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }

        throw new IllegalArgumentException("일치하는 카테고리 없음");
    }
}