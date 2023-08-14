package com.aliens.friendship.domain.market.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateMarketArticleCommentRequest {
    private String content;
}