package com.aliens.friendship.domain.article.service;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.repository.CommunityArticleRepository;
import com.aliens.db.communityarticlecomment.repository.CommunityArticleCommentRepository;
import com.aliens.db.marketarticle.repository.MarketArticleRepository;
import com.aliens.db.marketarticlecomment.repository.MarketArticleCommentRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.article.community.service.CommunityArticleService;
import com.aliens.friendship.domain.article.dto.ArticleCategoryDto;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.domain.article.market.service.MarketArticleService;
import com.aliens.friendship.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final CommunityArticleRepository communityArticleRepository;
    private final CommunityArticleService communityArticleService;
    private final MarketArticleRepository marketArticleRepository;
    private final MarketArticleService marketArticleService;
    private final MemberService memberService;
    private final CommunityArticleCommentRepository communityArticleCommentRepository;
    private final MarketArticleCommentRepository marketArticleCommentRepository;

    /**
     * 게시판 카테고리 전체 조회
     */
    public ArticleCategoryDto getAllArticleCategories() {
        return ArticleCategoryDto.of(
                Arrays.stream(ArticleCategory.values()).collect(Collectors.toList())
        );
    }

    /**
     * 전체 게시판 검색
     */
    public Page<ArticleDto> searchArticles(
            Pageable pageable,
            String searchKeyword
    ) {
        List<ArticleDto> communityArticles = searchCommunityArticle(searchKeyword);
        List<ArticleDto> marketArticles = searchMarketArticle(searchKeyword);

        List<ArticleDto> results = new ArrayList<>();
        results.addAll(communityArticles);
        results.addAll(marketArticles);
        results.sort((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }

    private List<ArticleDto> searchCommunityArticle(
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return communityArticleRepository.findAll()
                    .stream()
                    .map(article ->
                            ArticleDto.from(
                                    article,
                                    communityArticleService.getCommunityArticleLikesCount(article),
                                    communityArticleService.getCommunityArticleCommentCount(article),
                                    communityArticleService.getCommunityArticleImages(article)
                            )
                    ).collect(Collectors.toList());
        }

        return communityArticleRepository.findAllByTitleContainingOrContentContaining(
                        searchKeyword,
                        searchKeyword
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        communityArticleService.getCommunityArticleLikesCount(article),
                        communityArticleService.getCommunityArticleCommentCount(article),
                        communityArticleService.getCommunityArticleImages(article)
                ))
                .collect(Collectors.toList());
    }

    private List<ArticleDto> searchMarketArticle(
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return marketArticleRepository.findAll()
                    .stream()
                    .map(article ->
                            ArticleDto.from(
                                    article,
                                    marketArticleService.getMarketArticleBookmarkCount(article),
                                    marketArticleService.getMarketArticleCommentsCount(article),
                                    marketArticleService.getMarketArticleImages(article)
                            )
                    ).collect(Collectors.toList());
        }

        return marketArticleRepository.findAllByTitleContainingOrContentContaining(
                        searchKeyword,
                        searchKeyword
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        marketArticleService.getMarketArticleBookmarkCount(article),
                        marketArticleService.getMarketArticleCommentsCount(article),
                        marketArticleService.getMarketArticleImages(article)
                ))
                .collect(Collectors.toList());
    }


    /**
     * 내가 작성한 전체 게시글 검색
     */
    public Page<ArticleDto> searchMyArticles(
            Pageable pageable
    ) throws Exception {
        MemberEntity loginMemberEntity = memberService.getCurrentMemberEntity();

        List<ArticleDto> communityArticles = searchMyCommunityArticle(loginMemberEntity);
        List<ArticleDto> marketArticles = searchMyMarketArticle(loginMemberEntity);

        List<ArticleDto> results = new ArrayList<>();
        results.addAll(communityArticles);
        results.addAll(marketArticles);
        results.sort((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }

    private List<ArticleDto> searchMyCommunityArticle(
            MemberEntity loginMemberEntity
    ) {
        return communityArticleRepository.findAllByMember(
                        loginMemberEntity
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        communityArticleService.getCommunityArticleLikesCount(article),
                        communityArticleService.getCommunityArticleCommentCount(article),
                        communityArticleService.getCommunityArticleImages(article)
                ))
                .collect(Collectors.toList());
    }

    private List<ArticleDto> searchMyMarketArticle(
            MemberEntity loginMemberEntity
    ) {
        return marketArticleRepository.findAllByMember(
                        loginMemberEntity
                )
                .stream()
                .map(article -> ArticleDto.from(
                        article,
                        marketArticleService.getMarketArticleBookmarkCount(article),
                        marketArticleService.getMarketArticleCommentsCount(article),
                        marketArticleService.getMarketArticleImages(article)
                ))
                .collect(Collectors.toList());
    }



    /**
     * 내가 작성한 댓글을 포함한 전체 게시글 검색
     */
    public Page<ArticleDto> searchArticlesByMyComments(
            Pageable pageable
    ) throws Exception {
        MemberEntity loginMemberEntity = memberService.getCurrentMemberEntity();

        List<ArticleDto> communityArticles = searchCommunityArticleByMyComments(loginMemberEntity);
        List<ArticleDto> marketArticles = searchMarketArticleByMyComments(loginMemberEntity);

        Set<ArticleDto> uniqueResults = new HashSet<>();
        uniqueResults.addAll(communityArticles);
        uniqueResults.addAll(marketArticles);

        List<ArticleDto> results = new ArrayList<>(uniqueResults);
        results.sort((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }

    private List<ArticleDto> searchCommunityArticleByMyComments(
            MemberEntity loginMemberEntity
    ) {
        return communityArticleCommentRepository.findAllByMember(
                        loginMemberEntity
                )
                .stream()
                .map(comment -> ArticleDto.from(
                        comment.getCommunityArticle(),
                        communityArticleService.getCommunityArticleLikesCount(comment.getCommunityArticle()),
                        communityArticleService.getCommunityArticleCommentCount(comment.getCommunityArticle()),
                        communityArticleService.getCommunityArticleImages(comment.getCommunityArticle())
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ArticleDto> searchMarketArticleByMyComments(
            MemberEntity loginMemberEntity
    ) {
        return marketArticleCommentRepository.findAllByMember(
                        loginMemberEntity
                )
                .stream()
                .map(comment -> ArticleDto.from(
                        comment.getMarketArticle(),
                        marketArticleService.getMarketArticleBookmarkCount(comment.getMarketArticle()),
                        marketArticleService.getMarketArticleCommentsCount(comment.getMarketArticle()),
                        marketArticleService.getMarketArticleImages(comment.getMarketArticle())
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 내가 좋아요, 찜한 게시글 목록 조회
     */
    public Page<ArticleDto> searchArticlesByMyLike(
            Pageable pageable
    ) throws Exception {
        MemberEntity loginMemberEntity = memberService.getCurrentMemberEntity();
        List<ArticleDto> marketArticles = marketArticleService.getAllBookmarks(loginMemberEntity);
        List<ArticleDto> communityArticles = communityArticleService.getAllLikes(loginMemberEntity);

        List<ArticleDto> results = new ArrayList<>();
        results.addAll(communityArticles);
        results.addAll(marketArticles);
        results.sort((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }

}