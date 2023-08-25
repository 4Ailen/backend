package com.aliens.db.communityarticlecomment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.packit.packit.domain.article.comment.constant.CommentType;
import site.packit.packit.domain.article.comment.constant.CommentStatus;
import site.packit.packit.domain.article.community.entity.CommunityArticle;
import site.packit.packit.domain.member.entity.MemberEntity;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityArticleCommentEntity
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
    private CommunityArticle communityArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private CommunityArticleCommentEntity(
            String content,
            CommentType type,
            CommunityArticle communityArticle,
            Long parentCommentId,
            MemberEntity member
    ) {
        this.content = content;
        this.type = type;
        this.communityArticle = communityArticle;
        this.parentCommentId = parentCommentId;
        this.member = member;
    }

    public static CommunityArticleCommentEntity of(
            String content,
            CommentType type,
            CommunityArticle marketArticle,
            Long parentCommentId,
            MemberEntity member
    ) {
        return new CommunityArticleCommentEntity(
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