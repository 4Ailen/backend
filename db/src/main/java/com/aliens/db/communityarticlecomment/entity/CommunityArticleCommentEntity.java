package com.aliens.db.communityarticlecomment.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
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
public class CommunityArticleCommentEntity
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
    private CommunityArticleEntity communityArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private CommunityArticleCommentEntity(
            String content,
            CommentType type,
            CommunityArticleEntity communityArticle,
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
            CommunityArticleEntity marketArticle,
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