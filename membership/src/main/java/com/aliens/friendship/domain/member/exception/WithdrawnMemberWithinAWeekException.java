package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class WithdrawnMemberWithinAWeekException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public WithdrawnMemberWithinAWeekException() {
        super(MemberExceptionCode.WITHDRAWN_MEMBER_WITHIN_A_WEEK.getMessage());
        exceptionCode = MemberExceptionCode.WITHDRAWN_MEMBER_WITHIN_A_WEEK;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}