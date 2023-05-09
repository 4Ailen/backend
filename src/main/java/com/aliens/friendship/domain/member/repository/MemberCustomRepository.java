package com.aliens.friendship.domain.member.repository;

import com.aliens.friendship.domain.member.domain.Member;

import java.util.Optional;

public interface MemberCustomRepository {
    Optional<Member> findByUsernameWithAuthority(String email);
}
