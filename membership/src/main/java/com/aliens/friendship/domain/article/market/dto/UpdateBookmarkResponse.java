package com.aliens.friendship.domain.article.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateBookmarkResponse {

    private Integer marketArticleBookmarkCount;
    private boolean isBookMarked;

    public boolean getIsBookMarked() {
        return isBookMarked;
    }
}
