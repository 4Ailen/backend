package com.aliens.db.notice.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class NoticeEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private NoticeEntity(
            String title,
            String content,
            MemberEntity member
    ) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public static NoticeEntity of(
            String title,
            String content,
            MemberEntity member
    ) {
        return new NoticeEntity(
                title,
                content,
                member
        );
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}