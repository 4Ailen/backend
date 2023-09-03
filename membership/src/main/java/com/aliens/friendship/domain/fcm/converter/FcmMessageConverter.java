package com.aliens.friendship.domain.fcm.converter;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.personalNotice.entity.PersonalNoticeEntity;
import com.aliens.friendship.global.common.annotation.Converter;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Converter
public class FcmMessageConverter {

    public MulticastMessage toArticleLikeNotice(PersonalNoticeEntity personalNotice, List<String> writerFcmTokens) {
        MemberEntity likedMember = personalNotice.getSender();

        return MulticastMessage.builder()
                .putData("type", personalNotice.getNoticeType().toString())
                .putData("articleCategory", personalNotice.getArticleCategory().getValue())
                .putData("profileImage", likedMember.getProfileImageUrl())
                .putData("name", likedMember.getName())
                .putData("nationality", likedMember.getNationality())
                .putData("createdAt", personalNotice.getCreatedAt().toString())
                .putData("articleUrl", personalNotice.getArticleUrl())
                .addAllTokens(writerFcmTokens)
                .build();
    }

    public MulticastMessage toArticleCommentNotice(PersonalNoticeEntity personalNotice, List<String> writerFcmTokens) {
        MemberEntity commentedMember = personalNotice.getSender();

        return MulticastMessage.builder()
                .putData("type", personalNotice.getNoticeType().toString())
                .putData("articleCategory", personalNotice.getArticleCategory().getValue())
                .putData("comment", personalNotice.getComment())
                .putData("profileImage", commentedMember.getProfileImageUrl())
                .putData("name", commentedMember.getName())
                .putData("nationality", commentedMember.getNationality())
                .putData("createdAt", personalNotice.getCreatedAt().toString())
                .putData("articleUrl", personalNotice.getArticleUrl())
                .addAllTokens(writerFcmTokens)
                .build();
    }

    public MulticastMessage toArticleCommentReplyNotice(PersonalNoticeEntity personalNotice, List<String> fcmTokens) {
        MemberEntity commentRepliedMember = personalNotice.getSender();

        return MulticastMessage.builder()
                .putData("type", personalNotice.getNoticeType().toString())
                .putData("articleCategory", personalNotice.getArticleCategory().getValue())
                .putData("comment", personalNotice.getComment())
                .putData("profileImage", commentRepliedMember.getProfileImageUrl())
                .putData("name", commentRepliedMember.getName())
                .putData("nationality", commentRepliedMember.getNationality())
                .putData("createdAt", personalNotice.getCreatedAt().toString())
                .putData("articleUrl", personalNotice.getArticleUrl())
                .addAllTokens(fcmTokens)
                .build();
    }
}
