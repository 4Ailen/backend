package com.aliens.friendship.matching.domain;

import com.aliens.friendship.member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne( optional = false, cascade=CascadeType.REMOVE)
    @JoinColumn(name = "blocked_member_id", nullable = false)
    private Member blockedMember;

    @ManyToOne( optional = false, cascade=CascadeType.REMOVE)
    @JoinColumn(name = "blocking_member_id", nullable = false)
    private Member blockingMember;

}