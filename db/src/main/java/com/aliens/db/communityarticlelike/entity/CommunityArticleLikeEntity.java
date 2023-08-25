package com.aliens.db.communityarticlelike.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.MemberEntity;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityArticleLikeEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityArticle communityArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    private CommunityArticleLikeEntity(
            CommunityArticle communityArticle,
            MemberEntity member
    ) {
        this.communityArticle = communityArticle;
        this.memberEntity = member;
    }

    public static CommunityArticleLikeEntity of(
            CommunityArticle communityArticle,
            MemberEntity member
    ) {
        return new CommunityArticleLikeEntity(
                communityArticle,
                member
        );
    }
}