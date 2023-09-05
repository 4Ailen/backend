package com.aliens.db.marketarticle;

import com.aliens.db.communityarticle.ArticleCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MarketArticleStatus {
    SELL("판매 중"), END("판매 완료");

    private final String value;

    public String getValue() {
        return value;
    }

    public static MarketArticleStatus of(String value) {
        for (MarketArticleStatus status : MarketArticleStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("일치하는 판매상태 없음");
    }
}