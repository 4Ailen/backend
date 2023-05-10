package com.aliens.friendship.domain.matching.domain;

import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Matching.TABLE_NAME, schema = "aliendb")
public class Matching {
    public static final String TABLE_NAME = "matching";
    public static final String COLUMN_ID_NAME = "chatting_id";
    public static final String COLUMN_APPLICANT_NAME = "applicant_id";
    public static final String COLUMN_CHATTINGROOM_NAME = "chatting_room_id";

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_APPLICANT_NAME, nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_CHATTINGROOM_NAME, nullable = false)
    private ChattingRoom chattingRoom;

}