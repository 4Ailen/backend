package com.aliens.db.member.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.auth.entity.AuthorityEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity @Getter @ToString @SuperBuilder
public class MemberEntity extends BaseEntity {

    @Column(nullable = false, length = 45)
    private String email;

    @Column( nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private Mbti mbti;

    @Column(nullable = false, length = 45)
    private String gender;

    @JoinColumn( nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String birthday;

    @Column(nullable = false, length = 45)
    private String name;

    @Column( nullable = false)
    @Builder.Default
    private String selfIntroduction = "Nice to meet you!";

    @Column(nullable = false)
    @Builder.Default
    private String profileImageUrl = "/default_image.jpg";

    @Column( nullable = false)
    @Builder.Default
    private Byte notificationStatus = 0;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false, length = 45)
    @Builder.Default
    private Status status = Status.NotAppliedAndNotMatched;

    private Instant withdrawalAt;

    @OneToMany(mappedBy = "memberEntity",
            cascade = ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)

    @Builder.Default
    private Set<AuthorityEntity> authorities = new HashSet<>();

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateMbti(Mbti mbti) {
        this.mbti = mbti;
    }

    public void updateImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void updateSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public void updateWithdrawalDate(Instant withdrawalAt) {
        this.withdrawalAt = withdrawalAt;
    }

    public void addAuthority(AuthorityEntity authorityEntity) {
        authorities.add(authorityEntity);
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(AuthorityEntity::getRole)
                .collect(toList());
    }

    public int getAge() throws Exception {
        Calendar birthCalendar = getCalendarFromString(birthday);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    private Calendar getCalendarFromString(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = format.parse(date);
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

        return birthCalendar;
    }

    public enum Status {
        NotAppliedAndMatched,  // 미신청_매칭된 상태

        NotAppliedAndNotMatched,  // 미신청_매칭되지 않은 상태

        AppliedAndNotMatched,  // 신청_매칭되지 않은 상태

        AppliedAndMatched, // 신청_매칭된 상태

        WITHDRAWN;

    }

    public enum Mbti {
        ISTJ, ISFJ, INFJ, INTJ, ISTP, ISFP, INFP, INTP, ESTP, ESFP, ENFP, ENTP, ESTJ, ESFJ, ENFJ, ENTJ
    }
}
