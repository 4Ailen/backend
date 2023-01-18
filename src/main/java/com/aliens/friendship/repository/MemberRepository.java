package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m join fetch m.authorities a where m.email = :email")
    Optional<Member> findByUsernameWithAuthority(String email);
    
}