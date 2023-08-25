package com.aliens.db.marketarticlecomment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.packit.packit.domain.article.comment.constant.CommentStatus;
import site.packit.packit.domain.article.comment.constant.CommentType;
import site.packit.packit.domain.article.market.entity.MarketArticle;
import site.packit.packit.domain.member.entity.MemberEntity;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketArticleCommentEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CommentType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Setter
    @Column(updatable = false)
    private Long parentCommentId; // 부모 댓글 ID

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticle marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private MarketArticleCommentEntity(
            String content,
            CommentType type,
            MarketArticle marketArticle,
            Long parentCommentId,
            MemberEntity member
    ) {
        this.content = content;
        this.type = type;
        this.marketArticle = marketArticle;
        this.parentCommentId = parentCommentId;
        this.member = member;
    }

    public static MarketArticleCommentEntity of(
            String content,
            CommentType type,
            MarketArticle marketArticle,
            Long parentCommentId,
            MemberEntity member
    ) {
        return new MarketArticleCommentEntity(
                content,
                type,
                marketArticle,
                parentCommentId,
                member
        );
    }

    public void update(String content) {
        this.content = content;
    }

    public void deleteComment() {
        this.status = CommentStatus.DELETE;
    }
}