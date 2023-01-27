package com.aliens.friendship.blockingInfo.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlockingInfo {
    private int blockingInfoId;
    private int blockedMemberId;
    private int blockingMemberId;

    @Builder
    public BlockingInfo(int blockingInfoId, int blockedMemberId, int blockingMemberId) {
        this.blockingInfoId = blockingInfoId;
        this.blockedMemberId = blockedMemberId;
        this.blockingMemberId = blockingMemberId;
    }
}
