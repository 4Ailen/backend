package com.aliens.friendship.domain.article.community.service;

import com.aliens.db.communityarticle.ArticleCategory;
import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticle.repository.CommunityArticleRepository;
import com.aliens.db.communityarticlecomment.repository.CommunityArticleCommentRepository;
import com.aliens.db.communityarticleimage.entity.CommunityArticleImageEntity;
import com.aliens.db.communityarticleimage.repository.CommunityArticleImageRepository;
import com.aliens.db.communityarticlelike.entity.CommunityArticleLikeEntity;
import com.aliens.db.communityarticlelike.repository.CommunityArticleLikeRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.article.community.dto.CreateCommunityArticleRequest;
import com.aliens.friendship.domain.article.community.dto.UpdateCommunityArticleRequest;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.article.exception.ArticleExceptionCode.ARTICLE_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@Transactional
@RequiredArgsConstructor
@Service
public class CommunityArticleService {

    private final CommunityArticleRepository communityArticleRepository;
    private final CommunityArticleImageRepository communityArticleImageRepository;
    private final CommunityArticleLikeRepository communityArticleLikeRepository;
    private final CommunityArticleCommentRepository communityArticleCommentRepository;
    private final MemberRepository memberRepository;

    /**
     * 커뮤니티 게시판 검색
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchCommunityArticles(
            Pageable pageable,
            ArticleCategory category,
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return communityArticleRepository.findAllByCategory(category, pageable)
                    .map(article ->
                            ArticleDto.from(
                                    article,
                                    getCommunityArticleLikesCount(article),
                                    getCommunityArticleCommentCount(article),
                                    getCommunityArticleImages(article)
                            )
                    );
        }

        return communityArticleRepository.findAllByCategoryAndTitleContainingOrContentContaining(
                        category,
                        searchKeyword,
                        searchKeyword,
                        pageable
                )
                .map(article -> ArticleDto.from(
                        article,
                        getCommunityArticleLikesCount(article),
                        getCommunityArticleCommentCount(article),
                        getCommunityArticleImages(article)
                ));
    }

    /**
     * 커뮤니티 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public ArticleDto getCommunityArticle(Long articleId) {
        CommunityArticleEntity communityArticle = getCommunityArticleEntity(articleId);

        return ArticleDto.from(
                communityArticle,
                getCommunityArticleLikesCount(communityArticle),
                getCommunityArticleCommentCount(communityArticle),
                getCommunityArticleImages(communityArticle)
        );
    }

    public Long saveCommunityArticle(
            CreateCommunityArticleRequest request,
            UserDetails principal
    ) {
        CommunityArticleEntity communityArticle = communityArticleRepository.save(
                request.toEntity(getMemberEntity(principal.getUsername()))
        );

        for (String imageUrl : request.getImageUrls()) {
            CommunityArticleImageEntity communityArticleImage = CommunityArticleImageEntity.of(
                    imageUrl,
                    communityArticle
            );
            communityArticleImageRepository.save(communityArticleImage);
        }

        return communityArticle.getId();
    }

    public void updateCommunityArticle(
            Long articleId,
            UpdateCommunityArticleRequest request,
            UserDetails principal
    ) {
        CommunityArticleEntity savedCommunityArticle = getCommunityArticleEntity(articleId);

        verifyResourceOwner(savedCommunityArticle, getMemberEntity(principal.getUsername()));

        savedCommunityArticle.update(
                request.getTitle(),
                request.getContent()
        );

        communityArticleImageRepository.deleteAllByCommunityArticle(savedCommunityArticle);

        for (String imageUrl : request.getImageUrls()) {
            CommunityArticleImageEntity communityArticleImage = CommunityArticleImageEntity.of(
                    imageUrl,
                    savedCommunityArticle
            );
            communityArticleImageRepository.save(communityArticleImage);
        }
    }

    public void deleteCommunityArticle(
            Long articleId,
            UserDetails principal
    ) {
        CommunityArticleEntity savedCommunityArticle = getCommunityArticleEntity(articleId);

        verifyResourceOwner(savedCommunityArticle, getMemberEntity(principal.getUsername()));

        communityArticleImageRepository.deleteAllByCommunityArticle(savedCommunityArticle);
        communityArticleRepository.delete(savedCommunityArticle);
    }

    public void createArticleLike(
            Long articleId,
            UserDetails principal
    ) {
        CommunityArticleEntity communityArticle = getCommunityArticleEntity(articleId);
        MemberEntity member = getMemberEntity(principal.getUsername());

        communityArticleLikeRepository.save(
                CommunityArticleLikeEntity.of(
                        communityArticle,
                        member
                )
        );
    }

    public void deleteArticleLike(
            Long articleId,
            UserDetails principal
    ) {
        communityArticleLikeRepository.deleteAllByCommunityArticleAndMemberEntity(
                getCommunityArticleEntity(articleId),
                getMemberEntity(principal.getUsername())
        );
    }

    @Transactional(readOnly = true)
    public List<ArticleDto> getAllLikes(
            UserDetails principal
    ) {
        List<CommunityArticleEntity> communityArticles = communityArticleLikeRepository.findAllByMemberEntity(
                        getMemberEntity(principal.getUsername())
                )
                .stream()
                .map(CommunityArticleLikeEntity::getCommunityArticle)
                .collect(Collectors.toList());

        List<ArticleDto> results = new ArrayList<>();
        for (CommunityArticleEntity communityArticle : communityArticles) {
            List<String> images = communityArticleImageRepository.findAllByCommunityArticle(communityArticle)
                    .stream()
                    .map(CommunityArticleImageEntity::getImageUrl)
                    .collect(Collectors.toList());
            results.add(
                    ArticleDto.from(
                            communityArticle,
                            getCommunityArticleLikesCount(communityArticle),
                            getCommunityArticleCommentCount(communityArticle),
                            images
                    )
            );
        }
        return results;
    }

    public CommunityArticleEntity getCommunityArticleEntity(Long articleId) {
        return communityArticleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_NOT_FOUND));
    }

    public MemberEntity getMemberEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    public void verifyResourceOwner(
            CommunityArticleEntity communityArticle,
            MemberEntity member
    ) {
        if (!communityArticle.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }

    public Integer getCommunityArticleLikesCount(CommunityArticleEntity communityArticle) {
        return communityArticleLikeRepository.findAllByCommunityArticle(communityArticle).size();
    }

    public List<String> getCommunityArticleImages(CommunityArticleEntity communityArticle) {
        return communityArticleImageRepository.findAllByCommunityArticle(communityArticle)
                .stream()
                .map(CommunityArticleImageEntity::getImageUrl)
                .collect(Collectors.toList());
    }

    public Integer getCommunityArticleCommentCount(CommunityArticleEntity communityArticle) {
        return communityArticleCommentRepository.countAllByCommunityArticle(communityArticle);
    }
}