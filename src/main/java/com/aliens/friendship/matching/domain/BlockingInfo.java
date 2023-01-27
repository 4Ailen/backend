package com.aliens.friendship.matching.domain;

import com.aliens.friendship.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = BlockingInfo.TABLE_NAME, schema = "aliendb")
public class BlockingInfo {
    public static final String TABLE_NAME = "blocking_info";
    public static final String COLUMN_ID_NAME = "blocking_info_id";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocked_member_id", nullable = false)
    private Member blockedMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocking_member_id", nullable = false)
    private Member blockingMember;

}