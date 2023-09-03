package com.aliens.db.marketarticle;

import com.aliens.db.communityarticle.ArticleCategory;
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

    public static ProductStatus of(String category) {
        for (ProductStatus ba : ProductStatus.values()) {
            if (ba.value.equals(category)) {
                return ba;
            }
        }

        throw new IllegalArgumentException("일치하는 상품상태 없음");
    }
}