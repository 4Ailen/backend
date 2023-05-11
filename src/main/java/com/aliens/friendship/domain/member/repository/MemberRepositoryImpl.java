package com.aliens.friendship.domain.member.repository;

import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByUsernameWithAuthority(String email) {
        QMember qMember = QMember.member;
        Member member = queryFactory
                .selectFrom(qMember)
                .join(qMember.authorities).fetchJoin()
                .where(qMember.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(member);
    }

}
