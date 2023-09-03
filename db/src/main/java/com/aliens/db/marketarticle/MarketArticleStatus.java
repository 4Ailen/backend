package com.aliens.db.marketarticle;

import com.aliens.db.communityarticle.ArticleCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MarketArticleStatus {
    SELL("판매 중"), END("판매 완료");

    private final String value;

    public static MarketArticleStatus from(String value) {
        for (MarketArticleStatus status : MarketArticleStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public static MarketArticleStatus of(String category) {
        for (MarketArticleStatus ba : MarketArticleStatus.values()) {
            if (ba.value.equals(category)) {
                return ba;
            }
        }

        throw new IllegalArgumentException("일치하는 판매상태 없음");
    }
}