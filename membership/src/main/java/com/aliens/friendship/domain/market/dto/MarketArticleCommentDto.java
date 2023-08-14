package com.aliens.friendship.domain.market.dto;

import com.aliens.friendship.domain.market.entity.MarketArticleComment;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MarketArticleCommentDto {

    private Long marketArticleCommentId;
    private String content;
    private MemberDto member;

    public static MarketArticleCommentDto from(
            MarketArticleComment marketArticleComment
    ) {
        return new MarketArticleCommentDto(
                marketArticleComment.getId(),
                marketArticleComment.getContent(),
                MemberDto.from(marketArticleComment.getMember())
        );
    }
}