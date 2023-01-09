package com.aliens.friendship.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "blocking_info", schema = "aliendb")
public class BlockingInfo {
    @Id
    @Column(name = "blocking_info_id", nullable = false)
    private Integer id;

    @Column(name = "blocked_member_id", nullable = false)
    private Integer blockedMemberId;

    @Column(name = "blocking_member_id", nullable = false)
    private Integer blockingMemberId;

}