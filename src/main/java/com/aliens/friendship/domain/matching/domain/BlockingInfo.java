package com.aliens.friendship.domain.matching.domain;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

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

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @ManyToOne( optional = false)
    @JoinColumn(name = "blocked_member_id", nullable = false)
    private Member blockedMember;

    @ManyToOne( optional = false)
    @JoinColumn(name = "blocking_member_id", nullable = false)
    private Member blockingMember;

}