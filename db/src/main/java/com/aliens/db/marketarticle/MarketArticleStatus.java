package com.aliens.db.marketarticle;

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
}