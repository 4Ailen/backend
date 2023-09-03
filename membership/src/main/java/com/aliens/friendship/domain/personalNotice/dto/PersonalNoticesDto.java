package com.aliens.friendship.domain.personalNotice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PersonalNoticesDto {
    public List<PersonalNoticeInfo> personalNoticeInfos;

    public PersonalNoticesDto() {
        this.personalNoticeInfos = new ArrayList<>();
    }

    @Getter
    @Builder
    public static class PersonalNoticeInfo {
        private Long personalNoticeId;
        private String noticeType;
        private String articleCategory;
        private String comment;
        private String profileImage;
        private String name;
        private String nationality;
        private Instant createdAt;
        private String articleUrl;
        private Boolean isRead;
    }
}
