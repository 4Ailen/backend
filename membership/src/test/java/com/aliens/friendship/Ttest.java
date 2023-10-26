package com.aliens.friendship;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticle.repository.CommunityArticleRepository;
import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.communityarticlecomment.repository.CommunityArticleCommentRepository;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.db.communityarticlelike.repository.CommunityArticleLikeRepository;
import com.aliens.db.marketarticlecomment.CommentType;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.article.service.ArticleService;
import com.aliens.friendship.domain.member.business.MemberBusiness;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@SpringBootTest
@Slf4j
public class Ttest {
    @Autowired
    ArticleService articleService;

    @Autowired
    MemberBusiness memberBusiness;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommunityArticleRepository communityArticleRepository;

    @Autowired
    CommunityArticleCommentRepository communityCommentRepository;

    @Autowired
    CommunityArticleLikeRepository communityArticleLikeRepository;

    @Test
    public void testSearchMarketArticles() {
        String searchKeyword = "";
        Pageable pageable = PageRequest.of(0, 10); // Set the page and size as needed

        long startTime = System.currentTimeMillis();
        articleService.searchArticles(pageable, searchKeyword);
        long endTime = System.currentTimeMillis();

        System.out.println("Search time: " + (endTime - startTime) + "ms");

        // Perform assertions on the result as needed
    }

    @Test
    public void testSearchMarketArticlesWithFetchJoin() {
        String searchKeyword = "";
        Pageable pageable = PageRequest.of(0, 10); // Set the page and size as needed

        long startTime = System.currentTimeMillis();
        articleService.searchMyArticlesWithFetchJoin(pageable, searchKeyword);
        long endTime = System.currentTimeMillis();

        System.out.println("Search with fetch join time: " + (endTime - startTime) + "ms");

        // Perform assertions on the result as needed
    }

    @Test
    public void makeMemberAndArticle() throws Exception {

        for (int i = 0; i < 1000; i++){
            MemberEntity memberEntity = MemberEntity.builder()
                    .email(i +"skatks@naver.com")
                    .name(i+"kim")
                    .birthday("1998-11-25")
                    .email("test@example.com")
                    .gender("male")
                    .mbti(MemberEntity.Mbti.ENFJ)
                    .nationality("KOREA")
                    .password("1234").build();
            memberRepository.save(memberEntity);

            CommunityArticleEntity communityArticleEntity = CommunityArticleEntity.of(
                    "hihi"+i,
                    "jey"+i,
                    ArticleCategory.FREE,
                    memberEntity
            );
            communityArticleRepository.save(communityArticleEntity);


            CommunityArticleCommentEntity commentEntity = CommunityArticleCommentEntity.of(
                    "hi",
                    CommentType.PARENT,
                    communityArticleEntity,
                    1L,
                    memberEntity
                    );
            communityCommentRepository.save(commentEntity);

            CommunityArticleLikeEntity likeEntity = CommunityArticleLikeEntity.of(
                    communityArticleEntity,
                    memberEntity
            );
            communityArticleLikeRepository.save(likeEntity);

        }
    }

}
