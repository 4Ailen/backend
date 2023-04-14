package com.aliens.friendship.member.domain;

import com.aliens.friendship.jwt.domain.Authority;
import com.aliens.friendship.member.controller.dto.JoinDto;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * email,password,mbti,gender,nationality,age,name
 */
@Entity @Getter @ToString @Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED) @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = Member.TABLE_NAME)
public class Member {

    public static final String TABLE_NAME = "member";
    public static final String COLUMN_ID_NAME = "member_id";
    public static final String COLUMN_EMAIL_NAME = "email";
    public static final String COLUMN_PASSWORD_NAME = "password";
    public static final String COLUMN_MBTI_NAME = "mbti";
    public static final String COLUMN_GENDER_NAME = "gender";
    public static final String COLUMN_BIRTHDAY_NAME = "birthday";
    public static final String COLUNM_NATIONALITY_NAME = "nationality";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_JOINDATE_NAME = "join_date";
    public static final String COLUMN_IMAGEURL_NAME = "image_url";
    public static final String COLUMN_NOTIFICATIONSTATUS_NAME = "notification_status";
    public static final String COLUMN_ISAPPLIED_NAME = "is_applied";
    public static final String COLUMN_ISWITHDRAWN_NAME = "is_withdrawn";

    @Id
    @GeneratedValue(strategy = IDENTITY)
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
    @JoinColumn(name = COLUNM_NATIONALITY_NAME, nullable = false)
    private Nationality nationality;

    @Column(name = COLUMN_BIRTHDAY_NAME, nullable = false)
    private String birthday;

    @Column(name = COLUMN_NAME_NAME, nullable = false, length = 45)
    private String name;

    @Column(name = COLUMN_JOINDATE_NAME, nullable = false)
    private Instant joinDate;

    @Column(name = COLUMN_IMAGEURL_NAME, nullable = false)
    @Builder.Default
    private String profileImageUrl = "/default_image.jpg";

    @Column(name = COLUMN_NOTIFICATIONSTATUS_NAME, nullable = false)
    @Builder.Default
    private Byte notificationStatus = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ISAPPLIED_NAME, nullable = false, length = 45)
    @Builder.Default
    private Status isApplied = Status.NOT_APPLIED;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ISWITHDRAWN_NAME, nullable = false, length = 45)
    @Builder.Default
    private Status isWithdrawn = Status.NOT_WITHDRAWN;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    public void updateIsApplied(Status status) {
        this.isApplied = status;
    }

    public void updateIsWithdrawn(Status status) {
        this.isWithdrawn = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateMbti(String mbti) {
        this.mbti = mbti;
    }

    public void updateImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public static Member ofUser(JoinDto joinDto) {
        Member member = Member.builder()
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .mbti(joinDto.getMbti())
                .gender(joinDto.getGender())
                .birthday(joinDto.getBirthday())
                .name(joinDto.getName())
                .nationality(joinDto.getNationality())
                .joinDate(Instant.now())
                .profileImageUrl(joinDto.getImageUrl())
                .build();
        member.addAuthority(Authority.ofUser(member));
        return member;
    }

    public static Member ofAdmin(JoinDto joinDto) {
        Member member = Member.builder()
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .mbti(joinDto.getMbti())
                .gender(joinDto.getGender())
                .birthday(joinDto.getBirthday())
                .name(joinDto.getName())
                .nationality(joinDto.getNationality())
                .joinDate(Instant.now())
                .build();
        member.addAuthority(Authority.ofAdmin(member));
        return member;
    }

    private void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(Authority::getRole)
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
        APPLIED, NOT_APPLIED, WITHDRAWN, NOT_WITHDRAWN;
    }


}