package com.aliens.friendship.domain.article.dto;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

import static com.aliens.db.communityarticle.ArticleCategory.MARKET;

@Getter
public class ArticleDto {

    private Long articleId;
    private ArticleCategory category;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer commentsCount;
    private List<String> imageUrls;
    private Instant createdAt;
    private MemberDto member;

    public ArticleDto(
            Long articleId,
            ArticleCategory category,
            String title,
            String content,
            Integer likeCount,
            Integer commentsCount,
            List<String> imageUrls,
            Instant createdAt,
            MemberDto member
    ) {
        this.articleId = articleId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
        this.member = member;
    }

    public static ArticleDto from(
            CommunityArticleEntity communityArticle,
            Integer likeCount,
            Integer commentCount,
            List<String> imageUrls
    ) {
        return new ArticleDto(
                communityArticle.getId(),
                communityArticle.getCategory(),
                communityArticle.getTitle(),
                communityArticle.getContent(),
                likeCount,
                commentCount,
                imageUrls,
                communityArticle.getCreatedAt(),
                MemberDto.from(communityArticle.getMember())
        );
    }

    public static ArticleDto from(
            MarketArticleEntity marketArticle,
            Integer likeCount,
            Integer commentCount,
            List<String> imageUrls
    ) {
        return new ArticleDto(
                marketArticle.getId(),
                MARKET,
                marketArticle.getTitle(),
                marketArticle.getContent(),
                likeCount,
                commentCount,
                imageUrls,
                marketArticle.getCreatedAt(),
                MemberDto.from(marketArticle.getMember())
        );
    }
}