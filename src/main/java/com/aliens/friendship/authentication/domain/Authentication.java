package com.aliens.friendship.authentication.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Authentication.TABLE_NAME, schema = "aliendb")
public class Authentication {
    public static final String TABLE_NAME = "authentication";
    public static final String COLUMN_ID_NAME = "authentication_id";
    public static final String COLUMN_CODE_NAME = "code";
    public static final String COLUMN_EMAIL_NAME = "email";
    public static final String COLUMN_CREATEDTIME_NAME = "created_time";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_CODE_NAME, nullable = false, length = 45)
    private String code;

    @Column(name = COLUMN_EMAIL_NAME, nullable = false, length = 45)
    private String email;

    @Column(name = COLUMN_CREATEDTIME_NAME, nullable = false)
    private Instant createdTime;

}