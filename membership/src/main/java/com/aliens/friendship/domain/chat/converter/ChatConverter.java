package com.aliens.friendship.domain.chat.converter;

import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Converter
public class ChatConverter {
    public ArrayList<Long> toRoomIds(List<MatchingEntity> matchEntities) {
        ArrayList<Long> chattingRoomIds = new ArrayList<>();
        for( MatchingEntity matchingEntity : matchEntities){
            chattingRoomIds.add(matchingEntity.getChattingRoomEntity().getId());
        }
        return chattingRoomIds;
    }
}
