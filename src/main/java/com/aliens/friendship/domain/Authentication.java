package com.aliens.friendship.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "authentication", schema = "aliendb")
public class Authentication {
    @Id
    @Column(name = "authentication_id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 45)
    private String code;

    @Column(name = "email", nullable = false)
    private Integer email;

    @Column(name = "status", nullable = false, length = 45)
    private String status;

    @Column(name = "created_time")
    private Instant createdTime;

}