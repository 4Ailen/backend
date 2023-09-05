package com.aliens.db.marketarticle;

import com.aliens.db.communityarticle.ArticleCategory;
import lombok.Getter;

@Getter
public enum ProductStatus {
    BRAND_NEW("새 것"),
    ALMOST_NEW("거의 새 것"),
    SLIGHT_DEFECT("약간의 하자"),
    USED("사용감 있음");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductStatus of(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("일치하는 상품상태 없음");
    }
}