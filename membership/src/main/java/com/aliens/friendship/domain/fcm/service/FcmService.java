package com.aliens.friendship.domain.fcm.service;

import com.aliens.db.auth.entity.FcmTokenEntity;
import com.aliens.db.auth.repository.FcmTokenRepository;
import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.communityarticlecomment.repository.CommunityArticleCommentRepository;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.db.marketarticlecomment.repository.MarketArticleCommentRepository;
import com.aliens.db.marketbookmark.entity.MarketBookmarkEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.personalNotice.entity.PersonalNoticeEntity;
import com.aliens.db.personalNotice.repository.PersonalNoticeRepository;
import com.aliens.friendship.domain.fcm.converter.FcmMessageConverter;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.config.fcm.FirebaseMessagingWrapper;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final PersonalNoticeRepository personalNoticeRepository;
    private final MarketArticleCommentRepository marketArticleCommentRepository;
    private final CommunityArticleCommentRepository communityArticleCommentRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessagingWrapper firebaseMessagingWrapper;
    private final FcmMessageConverter fcmMessageConverter;
    private final MemberService memberService;
    @Value("${file-server.domain}" + ":" + "${server.port}")
    private String domainUrlAndPort;

    /**
     * 게시글 좋아요 알림
     * 알림 대상: 게시글 작성자
     */
    public void sendArticleLikeNoticeToWriter(CommunityArticleLikeEntity communityArticleLikeEntity) throws Exception {
        CommunityArticleEntity communityArticle = communityArticleLikeEntity.getCommunityArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/community-articles/" + communityArticle.getId();
        if (!isRecipientCurrentUser(communityArticle.getMember())) {
            PersonalNoticeEntity personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_LIKE, communityArticle.getCategory(), communityArticleLikeEntity.getMemberEntity(), communityArticle.getMember(), articleUrl));

            // 알림 대상 토큰
            List<String> writerFcmTokens = getFcmTokens(communityArticle.getMember().getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList());

            if(!writerFcmTokens.isEmpty()){
                // 알림
                MulticastMessage message = fcmMessageConverter.toArticleLikeNotice(personalNotice, writerFcmTokens);
                firebaseMessagingWrapper.sendMulticast(message);
            }
        }
    }

    /**
     * 장터게시판 게시글 좋아요 알림
     * 알림 대상: 게시글 작성자
     */
    public void sendArticleLikeNoticeToWriter(MarketBookmarkEntity marketBookmarkEntity) throws Exception {
        MarketArticleEntity marketArticle = marketBookmarkEntity.getMarketArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/market-articles/" + marketArticle.getId();
        if (!isRecipientCurrentUser(marketArticle.getMember())) {
            PersonalNoticeEntity personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_LIKE, ArticleCategory.MARKET, marketBookmarkEntity.getMemberEntity(), marketArticle.getMember(), articleUrl));

            // 알림 대상 토큰
            List<String> writerFcmTokens = getFcmTokens(marketArticle.getMember().getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList());

            if(!writerFcmTokens.isEmpty()){
                // 알림
                MulticastMessage message = fcmMessageConverter.toArticleLikeNotice(personalNotice, writerFcmTokens);
                firebaseMessagingWrapper.sendMulticast(message);
            }
        }
    }

    /**
     * 게시글 댓글 알림
     * 알림 대상: 게시글 작성자
     */
    public void sendArticleCommentNoticeToWriter(CommunityArticleCommentEntity communityArticleCommentEntity) throws Exception {
        CommunityArticleEntity communityArticle = communityArticleCommentEntity.getCommunityArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/community-articles/" + communityArticle.getId();
        if (!isRecipientCurrentUser(communityArticle.getMember())) {
            PersonalNoticeEntity personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT, communityArticle.getCategory(), communityArticleCommentEntity.getContent(), communityArticleCommentEntity.getMember(), communityArticle.getMember(), articleUrl));

            // 알림 대상 토큰
            List<String> writerFcmTokens = getFcmTokens(communityArticle.getMember().getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList());

            if(!writerFcmTokens.isEmpty()){
                // 알림
                MulticastMessage message = fcmMessageConverter.toArticleCommentNotice(personalNotice, writerFcmTokens);
                firebaseMessagingWrapper.sendMulticast(message);
            }
        }
    }

    /**
     * 장터게시판 게시글 댓글 알림
     * 알림 대상: 게시글 작성자
     */
    public void sendArticleCommentNoticeToWriter(MarketArticleCommentEntity marketArticleCommentEntity) throws Exception {
        MarketArticleEntity marketArticle = marketArticleCommentEntity.getMarketArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/market-articles/" + marketArticle.getId();
        if (!isRecipientCurrentUser(marketArticle.getMember())) {
            PersonalNoticeEntity personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT, ArticleCategory.MARKET, marketArticleCommentEntity.getContent(), marketArticleCommentEntity.getMember(), marketArticle.getMember(), articleUrl));

            // 알림 대상 토큰
            List<String> writerFcmTokens = getFcmTokens(marketArticle.getMember().getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList());

            if(!writerFcmTokens.isEmpty()){
                // 알림
                MulticastMessage message = fcmMessageConverter.toArticleCommentNotice(personalNotice, writerFcmTokens);
                firebaseMessagingWrapper.sendMulticast(message);
            }
        }
    }

    /**
     * 게시글 대댓글 알림
     * 알림 대상: 게시글 작성자, 부모 댓글 작성자, 부모 댓글의 대댓글 작성자 모두
     */
    public void sendArticleCommentReplyNotice(CommunityArticleCommentEntity communityArticleCommentEntity) throws Exception {
        CommunityArticleEntity communityArticle = communityArticleCommentEntity.getCommunityArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/community-articles/" + communityArticle.getId();

        // 중복 알림 방지
        Set<Long> notifiedMemberIds = new HashSet<>();

        // 알림 대상 토큰
        MemberEntity communityArticleMember = communityArticle.getMember();
        Long parentCommentId = communityArticleCommentEntity.getParentCommentId();
        MemberEntity parentCommentMember = communityArticleCommentRepository.findById(parentCommentId).get().getMember();
        List<MemberEntity> parentCommentReplyMembers = communityArticleCommentRepository.findAllByParentCommentId(parentCommentId).stream().map(CommunityArticleCommentEntity::getMember).collect(Collectors.toList());
        List<String> fcmTokens = new ArrayList<>();
        PersonalNoticeEntity personalNotice = new PersonalNoticeEntity();
        if (!isRecipientCurrentUser(communityArticleMember)) {
            fcmTokens.addAll(getFcmTokens(communityArticleMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
            personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, communityArticle.getCategory(), communityArticleCommentEntity.getContent(), communityArticleCommentEntity.getMember(), communityArticleMember, articleUrl));
            notifiedMemberIds.add(communityArticleMember.getId());
        }
        if (!isRecipientCurrentUser(parentCommentMember) && !notifiedMemberIds.contains(parentCommentMember.getId())) {
            fcmTokens.addAll(getFcmTokens(parentCommentMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
            personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, communityArticle.getCategory(), communityArticleCommentEntity.getContent(), communityArticleCommentEntity.getMember(), parentCommentMember, articleUrl));
            notifiedMemberIds.add(parentCommentMember.getId());
        }
        for (MemberEntity parentCommentReplyMember : parentCommentReplyMembers) {
            if (!isRecipientCurrentUser(parentCommentReplyMember) && !notifiedMemberIds.contains(parentCommentReplyMember.getId())) {
                personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, communityArticle.getCategory(), communityArticleCommentEntity.getContent(), communityArticleCommentEntity.getMember(), parentCommentReplyMember, articleUrl));
                fcmTokens.addAll(getFcmTokens(parentCommentReplyMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
                notifiedMemberIds.add(parentCommentReplyMember.getId());
            }
        }

        if(!fcmTokens.isEmpty()){
            // 알림
            MulticastMessage message = fcmMessageConverter.toArticleCommentReplyNotice(personalNotice, fcmTokens);
            firebaseMessagingWrapper.sendMulticast(message);
        }
    }

    /**
     * 장터게시판 게시글 대댓글 알림
     * 알림 대상: 게시글 작성자, 부모 댓글 작성자, 부모 댓글의 대댓글 작성자 모두
     */
    public void sendArticleCommentReplyNotice(MarketArticleCommentEntity marketArticleCommentEntity) throws Exception {
        MarketArticleEntity marketArticle = marketArticleCommentEntity.getMarketArticle();
        String articleUrl = domainUrlAndPort + "/api/v2/market-articles/" + marketArticle.getId();

        // 중복 알림 방지
        Set<Long> notifiedMemberIds = new HashSet<>();

        // 알림 대상 토큰
        MemberEntity marketArticleMember = marketArticle.getMember();
        Long parentCommentId = marketArticleCommentEntity.getParentCommentId();
        MemberEntity parentCommentMember = marketArticleCommentRepository.findById(parentCommentId).get().getMember();
        List<MemberEntity> parentCommentReplyMembers = marketArticleCommentRepository.findAllByParentCommentId(parentCommentId).stream().map(MarketArticleCommentEntity::getMember).collect(Collectors.toList());
        List<String> fcmTokens = new ArrayList<>();
        PersonalNoticeEntity personalNotice = new PersonalNoticeEntity();
        if (!isRecipientCurrentUser(marketArticleMember)) {
            fcmTokens.addAll(getFcmTokens(marketArticleMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
            personalNotice = personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, ArticleCategory.MARKET, marketArticleCommentEntity.getContent(), marketArticleCommentEntity.getMember(), marketArticleMember, articleUrl));
            notifiedMemberIds.add(marketArticleMember.getId());
        }
        if (!isRecipientCurrentUser(parentCommentMember) && !notifiedMemberIds.contains(parentCommentMember.getId())) {
            fcmTokens.addAll(getFcmTokens(parentCommentMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
            personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, ArticleCategory.MARKET, marketArticleCommentEntity.getContent(), marketArticleCommentEntity.getMember(), parentCommentMember, articleUrl));
            notifiedMemberIds.add(parentCommentMember.getId());
        }
        for (MemberEntity parentCommentReplyMember : parentCommentReplyMembers) {
            if (!isRecipientCurrentUser(parentCommentReplyMember) && !notifiedMemberIds.contains(parentCommentReplyMember.getId())) {
                personalNoticeRepository.save(PersonalNoticeEntity.of(PersonalNoticeEntity.NoticeType.ARTICLE_COMMENT_REPLY, ArticleCategory.MARKET, marketArticleCommentEntity.getContent(), marketArticleCommentEntity.getMember(), parentCommentReplyMember, articleUrl));
                fcmTokens.addAll(getFcmTokens(parentCommentReplyMember.getId()).stream().map(FcmTokenEntity::getValue).collect(Collectors.toList()));
                notifiedMemberIds.add(parentCommentReplyMember.getId());
            }
        }

        if(!fcmTokens.isEmpty()){
            // 알림
            MulticastMessage message = fcmMessageConverter.toArticleCommentReplyNotice(personalNotice, fcmTokens);
            firebaseMessagingWrapper.sendMulticast(message);
        }
    }

    public List<FcmTokenEntity> getFcmTokens(Long memberId) {
        return fcmTokenRepository.findAllByMemberId(memberId);
    }

    private boolean isRecipientCurrentUser(MemberEntity recipientMember) throws Exception {
        return memberService.getCurrentMemberEntity().equals(recipientMember);
    }
}