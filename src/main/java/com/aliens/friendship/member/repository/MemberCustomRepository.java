package com.aliens.friendship.member.repository;

import com.aliens.friendship.member.domain.Member;

import java.util.Optional;

public interface MemberCustomRepository {
    Optional<Member> findByUsernameWithAuthority(String email);
}
