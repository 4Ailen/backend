package com.aliens.friendship.member.repository;

import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

//    @Query("select m from Member m join fetch m.authorities a where m.email = :email")
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
