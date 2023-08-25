package com.aliens.db.communityarticleimage.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private CommunityArticleEntity communityArticle;

    private CommunityArticleImageEntity(
            String imageUrl,
            CommunityArticleEntity communityArticle
    ) {
        this.imageUrl = imageUrl;
        this.communityArticle = communityArticle;
    }

    public static CommunityArticleImageEntity of(
            String imageUrl,
            CommunityArticleEntity communityArticle
    ) {
        return new CommunityArticleImageEntity(
                imageUrl,
                communityArticle
        );
    }
}