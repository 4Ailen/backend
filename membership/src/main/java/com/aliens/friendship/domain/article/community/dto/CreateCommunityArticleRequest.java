package com.aliens.friendship.domain.article.community.dto;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateCommunityArticleRequest {

    private String title;
    private String content;
    String category;
    private List<String> imageUrls;

    public CommunityArticleEntity toEntity(MemberEntity member) {
        return CommunityArticleEntity.of(
                title,
                content,
                ArticleCategory.of(category),
                member
        );
    }
}