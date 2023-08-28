package com.aliens.friendship.domain.article.comment.dto;

import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
public class ArticleCommentsDto {
    private Long articleCommentId;
    private String content;
    private Instant createdAt;
    private MemberDto member;
    private List<ArticleCommentDto> childs;

    public static ArticleCommentsDto from(
            ArticleCommentDto articleCommentDto,
            List<ArticleCommentDto> childs
    ) {
        return new ArticleCommentsDto(
                articleCommentDto.getArticleCommentId(),
                articleCommentDto.getContent(),
                articleCommentDto.getCreatedAt(),
                articleCommentDto.getMember(),
                childs
        );
    }
}