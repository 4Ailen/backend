package com.aliens.friendship.domain.market.constant;

import lombok.Getter;

@Getter
public enum ProductStatus {
    NEW("새 것"),
    MIDDLE_NEW("거의 새 것"),
    LITTLE_FLAW("약간의 하자"),
    USING("사용감 있음");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}