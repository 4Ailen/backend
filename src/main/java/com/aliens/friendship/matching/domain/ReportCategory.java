package com.aliens.friendship.matching.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportCategory {

    SEXUAL_HARASSMENT("SEXUAL_HARASSMENT", "성희롱"),
    VIOLENCE("VIOLENCE", "욕설/폭력"),
    SPAM("SPAM", "스팸/광고"),
    SCAM("SCAM", "사기"),
    ETC("ETC", "기타");

    private final String code;
    private final String message;
}
