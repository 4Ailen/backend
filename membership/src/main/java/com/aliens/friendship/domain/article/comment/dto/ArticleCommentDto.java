package com.aliens.friendship.domain.article.comment.dto;

import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

import static com.aliens.db.marketarticlecomment.CommentStatus.ACTIVE;


@AllArgsConstructor
@Getter
public class ArticleCommentDto {

    private Long articleCommentId;
    private String content;
    private Instant createdAt;
    private MemberDto member;

    public static ArticleCommentDto from(
            CommunityArticleCommentEntity articleComment
    ) {
        return new ArticleCommentDto(
                articleComment.getId(),
                articleComment.getStatus() == ACTIVE ? articleComment.getContent() : "삭제된 댓글입니다.",
                articleComment.getCreatedAt(),
                MemberDto.from(articleComment.getMember())
        );
    }

    public static ArticleCommentDto from(
            MarketArticleCommentEntity articleComment
    ) {
        return new ArticleCommentDto(
                articleComment.getId(),
                (articleComment.getStatus() == ACTIVE) ? articleComment.getContent() : "삭제된 댓글입니다.",
                articleComment.getCreatedAt(),
                MemberDto.from(articleComment.getMember())
        );
    }
}