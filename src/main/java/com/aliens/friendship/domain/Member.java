package com.aliens.friendship.domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "member", schema = "aliendb")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "mbti", nullable = false, length = 45)
    private String mbti;

    @Column(name = "gender", nullable = false, length = 45)
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nationality", nullable = false)
    private Nationality nationality;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "join_date", nullable = false)
    private Instant joinDate;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "notification_status", nullable = false)
    private Byte notificationStatus;

    @Column(name = "matching_status", nullable = false, length = 45)
    private String matchingStatus;
    
    
    
    // feature #13 jwt 관련 코드
    
    /**
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
    **/

}