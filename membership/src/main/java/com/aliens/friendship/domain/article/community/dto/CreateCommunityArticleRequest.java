package com.aliens.friendship.domain.article.community.dto;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.member.validation.ProfileImageValidate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class CreateCommunityArticleRequest {

    private String title;
    private String content;
    String category;

    @ProfileImageValidate
    @JsonIgnore
    private List<MultipartFile> imageUrls;

    public CommunityArticleEntity toEntity(MemberEntity member) {
        return CommunityArticleEntity.of(
                title,
                content,
                ArticleCategory.of(category),
                member
        );
    }
}