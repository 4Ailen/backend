package com.aliens.friendship.domain.notice.dto;

import com.aliens.db.notice.entity.NoticeEntity;
import com.aliens.friendship.domain.member.controller.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class NoticeDto {

    private Long noticeId;
    private String title;
    private String content;
    private Instant createdAt;
    private MemberDto memberDto;

    public static NoticeDto from(NoticeEntity noticeEntity) {
        return new NoticeDto(
                noticeEntity.getId(),
                noticeEntity.getTitle(),
                noticeEntity.getContent(),
                noticeEntity.getCreatedAt(),
                MemberDto.from(noticeEntity.getMember())
        );
    }
}