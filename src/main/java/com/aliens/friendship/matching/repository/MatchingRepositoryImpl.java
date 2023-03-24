package com.aliens.friendship.matching.repository;

import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.domain.Matching;
import com.aliens.friendship.matching.domain.QMatching;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MatchingRepositoryImpl implements MatchingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Integer> findPartnerIdsByApplicantId(Integer applicantId) {
        QMatching qMatching = QMatching.matching;
        List<Integer> chattingRoomIds = queryFactory
                .select(qMatching.chattingRoom.id)
                .from(qMatching)
                .where(qMatching.applicant.id.eq(applicantId))
                .fetch();
        List<Integer> partnerIds = queryFactory
                .select(qMatching.applicant.id)
                .from(qMatching)
                .where(qMatching.chattingRoom.id.in(chattingRoomIds).and(qMatching.applicant.id.ne(applicantId)))
                .fetch();
        return partnerIds;
    }


    @Override
    public Integer findPartnerIdByApplicantAndChattingRoom(Applicant applicant, ChattingRoom chattingRoom) {
        QMatching qMatching = QMatching.matching;
        Matching matching = queryFactory
                .selectFrom(qMatching)
                .where(qMatching.chattingRoom.id.eq(chattingRoom.getId()).and(qMatching.applicant.id.ne(applicant.getId())))
                .fetchOne();

        return matching.getApplicant().getId();
    }
}
