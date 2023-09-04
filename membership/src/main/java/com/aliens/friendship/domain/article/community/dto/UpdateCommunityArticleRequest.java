package com.aliens.friendship.domain.article.community.dto;

import com.aliens.friendship.domain.member.validation.ProfileImageValidate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class UpdateCommunityArticleRequest {

    private String title;
    private String content;
    @ProfileImageValidate
    @JsonIgnore
    private List<MultipartFile> imageUrls;
}