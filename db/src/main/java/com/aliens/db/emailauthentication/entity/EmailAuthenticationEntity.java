package com.aliens.db.emailauthentication.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor @AllArgsConstructor @SuperBuilder
@Entity @Getter @ToString
@Table(name = "emailAuthentication", schema = "aliendb")
public class EmailAuthenticationEntity {

    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false, length = 45)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 45)
    private Status status = Status.NOT_VERIFIED;

    @Column(nullable = false)
    private Instant expirationAt;

    @Column(nullable = false)
    private Instant createdAt;

    public enum Status {
        VERIFIED, NOT_VERIFIED;
    }
    public void updateStatus(Status status) {
        this.status = status;
    }
}