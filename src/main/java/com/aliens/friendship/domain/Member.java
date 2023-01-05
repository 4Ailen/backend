package com.aliens.friendship.domain;

import com.aliens.friendship.domain.dto.JoinDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
public class Member {

    @Id @GeneratedValue(strategy = IDENTITY) @Column(name = "MEMBER_ID")
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private Set<com.aliens.friendship.domain.Authority> authorities = new HashSet<>();

    public static Member ofUser(JoinDto joinDto) {
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .nickname(joinDto.getNickname())
                .build();
        member.addAuthority(com.aliens.friendship.domain.Authority.ofUser(member));
        return member;
    }

    public static Member ofAdmin(JoinDto joinDto) {
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .nickname(joinDto.getNickname())
                .build();
        member.addAuthority(com.aliens.friendship.domain.Authority.ofAdmin(member));
        return member;
    }

    private void addAuthority(com.aliens.friendship.domain.Authority authority) {
        authorities.add(authority);
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(com.aliens.friendship.domain.Authority::getRole)
                .collect(toList());
    }
}