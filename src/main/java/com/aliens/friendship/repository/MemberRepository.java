package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);

    @Query("select m from Member m join fetch m.authorities a where m.name = :name")
    Optional<Member> findByNameWithAuthority(String name);

}