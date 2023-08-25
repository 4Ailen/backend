package com.aliens.friendship.domain.notice.dto.request;

import lombok.Getter;

@Getter
public class CreateNoticeRequest {

    private String title;
    private String content;
}