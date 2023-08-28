package com.aliens.db.communityarticle.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityArticleEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ArticleCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

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