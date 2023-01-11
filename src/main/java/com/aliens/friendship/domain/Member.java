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

}