package com.aliens.db.communityarticleimage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityArticleImageEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityArticle communityArticle;

    private CommunityArticleImageEntity(
            String imageUrl,
            CommunityArticle communityArticle
    ) {
        this.imageUrl = imageUrl;
        this.communityArticle = communityArticle;
    }

    public static CommunityArticleImageEntity of(
            String imageUrl,
            CommunityArticle communityArticle
    ) {
        return new CommunityArticleImageEntity(
                imageUrl,
                communityArticle
        );
    }
}