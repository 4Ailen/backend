package com.aliens.db.communityarticle.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SuperBuilder
public class CommunityArticleEntity
        extends BaseEntity {

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ArticleCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    @OneToMany(mappedBy = "communityArticle", orphanRemoval = true)
    private List<CommunityArticleCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "communityArticle", orphanRemoval = true)
    private List<CommunityArticleLikeEntity> likes = new ArrayList<>();

    private CommunityArticleEntity(
            String title,
            String content,
            ArticleCategory category,
            MemberEntity member
    ) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public static CommunityArticleEntity of(
            String title,
            String content,
            ArticleCategory category,
            MemberEntity member
    ) {
        return new CommunityArticleEntity(
                title,
                content,
                category,
                member
        );
    }

    public void update(
            String title,
            String content
    ) {
        this.title = title;
        this.content = content;
    }
}