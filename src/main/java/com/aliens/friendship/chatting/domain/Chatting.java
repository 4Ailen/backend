package com.aliens.friendship.chatting.domain;

import com.aliens.friendship.matching.domain.MatchingParticipant;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Chatting.TABLE_NAME, schema = "aliendb")
public class Chatting {
    public static final String TABLE_NAME = "chatting";
    public static final String COLUMN_ID_NAME = "chatting_id";

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matching_participant_id", nullable = false)
    private MatchingParticipant matchingParticipant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chatting_room_id", nullable = false)
    private ChattingRoom chattingRoom;

}