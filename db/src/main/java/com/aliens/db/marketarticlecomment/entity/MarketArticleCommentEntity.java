package com.aliens.db.marketarticlecomment.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticlecomment.CommentStatus;
import com.aliens.db.marketarticlecomment.CommentType;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketArticleCommentEntity
        extends BaseEntity {

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
    private MarketArticleEntity marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private MarketArticleCommentEntity(
            String content,
            CommentType type,
            MarketArticleEntity marketArticle,
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
            MarketArticleEntity marketArticle,
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