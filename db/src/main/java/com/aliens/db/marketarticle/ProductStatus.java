package com.aliens.db.marketarticle;

import lombok.Getter;

@Getter
public enum ProductStatus {
    NEW("새 것"),
    MIDDLE_NEW("거의 새 것"),
    LITTLE_FLAW("약간의 하자"),
    USING("사용감 있음");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

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