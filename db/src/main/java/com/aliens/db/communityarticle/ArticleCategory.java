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

    public static ArticleCategory from(String value) {
        for (ArticleCategory status : ArticleCategory.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    private final String value;

    public static ArticleCategory of(String category) {
        if (category == null) {
            return ALL;
        }

        for (ArticleCategory ba : ArticleCategory.values()) {
            if (ba.value.equals(category)) {
                return ba;
            }
        }

        throw new IllegalArgumentException("일치하는 카테고리 없음");
    }
}