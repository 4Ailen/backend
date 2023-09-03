package com.aliens.friendship.domain.article.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateLikeResponse {

    private Integer likeCount;
    private boolean isLiked;

    public boolean getIsLiked() {
        return isLiked;
    }
}
