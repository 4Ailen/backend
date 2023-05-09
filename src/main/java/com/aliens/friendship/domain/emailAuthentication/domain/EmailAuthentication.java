package com.aliens.friendship.domain.emailAuthentication.domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = EmailAuthentication.TABLE_NAME, schema = "aliendb")
public class EmailAuthentication {
    public static final String TABLE_NAME = "email_authentication";
    public static final String COLUMN_ID_NAME = "authentication_id";
    public static final String COLUMN_EMAIL_NAME = "email";
    public static final String COLUMN_CREATEDTIME_NAME = "created_time";
    public static final String COLUMN_EXPIRATIONTIME_NAME = "expiration_time";

    public static final String COLUMN_STATUS_NAME = "status";
    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false, length = 36)
    private String id;

    @Column(name = COLUMN_EMAIL_NAME, nullable = false, length = 45)
    private String email;

    @Column(name = COLUMN_CREATEDTIME_NAME, nullable = false)
    private Instant createdTime;

    @Column(name = COLUMN_EXPIRATIONTIME_NAME, nullable = false)
    private Instant expirationTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = COLUMN_STATUS_NAME, nullable = false, length = 45)
    private Status status = Status.NOT_VERIFIED;

    public enum Status {
        VERIFIED, NOT_VERIFIED;
    }

    public static EmailAuthentication createEmailAuthentication(String email) {
        EmailAuthentication emailAuthentication = new EmailAuthentication();
        emailAuthentication.id = UUID.randomUUID().toString();
        emailAuthentication.email = email;
        emailAuthentication.createdTime = Instant.now();
        emailAuthentication.expirationTime = emailAuthentication.createdTime.plus(5, ChronoUnit.MINUTES);
        return emailAuthentication;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}