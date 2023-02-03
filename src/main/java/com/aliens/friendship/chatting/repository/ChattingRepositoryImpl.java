package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.Chatting;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.chatting.domain.QChatting;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChattingRepositoryImpl implements ChattingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findMatchingParticipantIdByMatchingParticipantAndChattingRoom(MatchingParticipant matchingParticipant, ChattingRoom chattingRoom) {
        QChatting qChatting = QChatting.chatting;
        Chatting chatting = queryFactory
                .selectFrom(qChatting)
                .where(qChatting.chattingRoom.id.eq(chattingRoom.getId()).and(qChatting.matchingParticipant.id.ne(matchingParticipant.getId())))
                .fetchOne();

        return chatting.getMatchingParticipant().getId();
    }

}
