package com.aliens.db.communityarticlelike.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityArticleLikeEntity
        extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityArticleEntity communityArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    private CommunityArticleLikeEntity(
            CommunityArticleEntity communityArticle,
            MemberEntity member
    ) {
        this.communityArticle = communityArticle;
        this.memberEntity = member;
    }

    public static CommunityArticleLikeEntity of(
            CommunityArticleEntity communityArticle,
            MemberEntity member
    ) {
        return new CommunityArticleLikeEntity(
                communityArticle,
                member
        );
    }
}