package com.aliens.friendship.domain;

import com.aliens.friendship.domain.dto.JoinDto;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = Member.TABLE_NAME, schema = "aliendb")
public class Member {
    @Setter(AccessLevel.NONE)
    public static final String TABLE_NAME = "member";
    public static final String COLUMN_ID_NAME = "member_id";
    public static final String COLUMN_EMAIL_NAME = "email";
    public static final String COLUMN_PASSWORD_NAME = "password";
    public static final String COLUMN_MBTI_NAME = "mbti";
    public static final String COLUMN_GENDER_NAME = "gender";
    public static final String COLUMN_AGE_NAME = "age";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_JOINDATE_NAME = "join_date";
    public static final String COLUMN_IMAGEURL_NAME = "image_url";
    public static final String COLUMN_NOTIFICATIONSTATUS_NAME = "notification_status";
    public static final String COLUMN_ISAPPLIED_NAME = "is_applied";


    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_EMAIL_NAME, nullable = false, length = 45)
    private String email;

    @Column(name = COLUMN_PASSWORD_NAME, nullable = false)
    private String password;

    @Column(name = COLUMN_MBTI_NAME, nullable = false, length = 45)
    private String mbti;

    @Column(name = COLUMN_GENDER_NAME, nullable = false, length = 45)
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nationality", nullable = false)
    private Nationality nationality;

    @Column(name = COLUMN_AGE_NAME, nullable = false)
    private Integer age;

    @Column(name = COLUMN_NAME_NAME, nullable = false, length = 45)
    private String name;

    @Column(name = COLUMN_JOINDATE_NAME, nullable = false)
    private Instant joinDate;

    @Column(name = COLUMN_IMAGEURL_NAME, nullable = false)
    private String imageUrl;

    @Column(name = COLUMN_NOTIFICATIONSTATUS_NAME, nullable = false)
    private Byte notificationStatus;

    @Column(name = COLUMN_ISAPPLIED_NAME, nullable = false, length = 45)
    private String isApplied;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    public static Member ofUser(JoinDto joinDto) {
        Member member = Member.builder()
                .name(UUID.randomUUID().toString())
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .build();
        member.addAuthority(com.aliens.friendship.domain.Authority.ofUser(member));
        return member;
    }

    public static Member ofAdmin(JoinDto joinDto) {
        Member member = Member.builder()
                .name(UUID.randomUUID().toString())
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
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
                .collect(Collectors.toList());
    }


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