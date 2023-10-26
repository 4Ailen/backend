package com.aliens.db.communityarticle;

import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticle.entity.QCommunityArticleEntity;
import com.aliens.db.communityarticlecomment.entity.QCommunityArticleCommentEntity;
import com.aliens.db.communityarticlelike.entity.QCommunityArticleLikeEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.entity.QMemberEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleSearchImpl extends QuerydslRepositorySupport implements ArticleSearch {
    public ArticleSearchImpl(){
        super(CommunityArticleEntity.class);
    }

    @Override
    public Long searchWithAll(){
        return 1L;
    }

    @Override
    public  Page<ArticleDto>searchWithFetchJoin(Pageable pageable, String searchKeyword){

        QCommunityArticleEntity communityArticleEntity = QCommunityArticleEntity.communityArticleEntity;
        QCommunityArticleCommentEntity communityArticleCommentEntity = QCommunityArticleCommentEntity.communityArticleCommentEntity;
        QCommunityArticleLikeEntity communityArticleLikeEntity = QCommunityArticleLikeEntity.communityArticleLikeEntity;
        QMemberEntity memberEntity = QMemberEntity.memberEntity;


        JPQLQuery<CommunityArticleEntity> communityJPQLQuery = from(communityArticleEntity);
        communityJPQLQuery.leftJoin(communityArticleCommentEntity).on(communityArticleCommentEntity.communityArticle.eq(communityArticleEntity));
        communityJPQLQuery.leftJoin(communityArticleLikeEntity).on(communityArticleLikeEntity.communityArticle.eq(communityArticleEntity));

        if( searchKeyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            booleanBuilder.or(communityArticleEntity.title.contains(searchKeyword));
            booleanBuilder.or(communityArticleEntity.content.contains(searchKeyword));

            communityJPQLQuery.where(booleanBuilder);
        }
        communityJPQLQuery.leftJoin(communityArticleEntity.member, memberEntity);

        communityJPQLQuery.groupBy(communityArticleEntity);

        getQuerydsl().applyPagination(pageable, communityJPQLQuery); //paging



        JPQLQuery<Tuple> tupleJPQLQuery = communityJPQLQuery.select(communityArticleEntity,
                communityArticleCommentEntity.countDistinct(),
                communityArticleLikeEntity.countDistinct(),
                memberEntity
                );

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<ArticleDto> dtoList = tupleList.stream().map(tuple -> {

            CommunityArticleEntity articleEntity = tuple.get(communityArticleEntity);
            MemberEntity memberEntityTu = tuple.get(memberEntity);
            long commentCount = tuple.get(1,Long.class);
            long likeCount = tuple.get(2,Long.class);

            ArticleDto dto = ArticleDto.from(
                    articleEntity,
                    (int) likeCount,
                    (int) commentCount,
                    List.of("1","2"),
                    memberEntityTu
            );

            return dto;
        }).collect(Collectors.toList());

        long totalCount = communityJPQLQuery.fetchCount();


        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}
