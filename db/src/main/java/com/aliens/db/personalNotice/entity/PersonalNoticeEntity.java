package com.aliens.db.personalNotice.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.member.entity.MemberEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "personal_notice", schema = "aliendb")
public class PersonalNoticeEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private NoticeType noticeType;

    @Column(length = 45, nullable = false)
    private ArticleCategory articleCategory;

    @Column(length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private MemberEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private MemberEntity receiver;

    private String articleUrl;

    private Boolean isRead;

    private PersonalNoticeEntity(
            NoticeType noticeType,
            ArticleCategory articleCategory,
            String comment,
            MemberEntity sender,
            MemberEntity receiver,
            String articleUrl
    ) {
        this.noticeType = noticeType;
        this.articleCategory = articleCategory;
        this.comment = comment;
        this.sender = sender;
        this.receiver = receiver;
        this.articleUrl = articleUrl;
        this.isRead = false;
    }

    public PersonalNoticeEntity(
            NoticeType noticeType,
            ArticleCategory articleCategory,
            MemberEntity sender,
            MemberEntity receiver,
            String articleUrl
    ) {
        this.noticeType = noticeType;
        this.articleCategory = articleCategory;
        this.sender = sender;
        this.receiver = receiver;
        this.articleUrl = articleUrl;
        this.isRead = false;
    }

    public static PersonalNoticeEntity of(
            NoticeType noticeType,
            ArticleCategory articleCategory,
            String comment,
            MemberEntity sender,
            MemberEntity receiver,
            String articleUrl
    ) {
        return new PersonalNoticeEntity(
                noticeType,
                articleCategory,
                comment,
                sender,
                receiver,
                articleUrl
        );
    }

    public static PersonalNoticeEntity of(
            NoticeType noticeType,
            ArticleCategory articleCategory,
            MemberEntity sender,
            MemberEntity receiver,
            String articleUrl
    ) {
        return new PersonalNoticeEntity(
                noticeType,
                articleCategory,
                sender,
                receiver,
                articleUrl
        );
    }

    public void updateIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public enum NoticeType {
        ARTICLE_COMMENT, ARTICLE_COMMENT_REPLY, ARTICLE_LIKE
    }
}
