package com.aliens.friendship.domain.article.community.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateCommunityArticleRequest {

    private String title;
    private String content;
    private List<String> imageUrls;
}