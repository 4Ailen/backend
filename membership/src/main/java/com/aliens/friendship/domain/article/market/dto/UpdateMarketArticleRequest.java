package com.aliens.friendship.domain.article.market.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class UpdateMarketArticleRequest {

    private String title;
    private String status;
    private Integer price;
    private String productStatus;
    private String content;
    @JsonIgnore
    private List<MultipartFile> imageUrls;
}