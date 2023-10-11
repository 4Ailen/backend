package com.aliens.friendship.domain.article.market.service;

import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticle.repository.MarketArticleRepository;
import com.aliens.db.marketarticlecomment.repository.MarketArticleCommentRepository;
import com.aliens.db.marketbookmark.entity.MarketBookmarkEntity;
import com.aliens.db.marketbookmark.repository.MarketBookmarkRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.db.productimage.entity.ProductImageEntity;
import com.aliens.db.productimage.repsitory.ProductImageRepository;
import com.aliens.friendship.domain.article.dto.ArticleDto;
import com.aliens.friendship.domain.article.market.dto.CreateMarketArticleRequest;
import com.aliens.friendship.domain.article.market.dto.MarketArticleDto;
import com.aliens.friendship.domain.article.market.dto.UpdateMarketArticleRequest;
import com.aliens.friendship.domain.article.service.ArticleImageService;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.article.exception.ArticleExceptionCode.ARTICLE_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@Transactional
@RequiredArgsConstructor
@Service
public class MarketArticleService {

    private final MarketArticleRepository marketArticleRepository;
    private final ProductImageRepository productImageRepository;
    private final MarketBookmarkRepository marketBookmarkRepository;
    private final MemberRepository memberRepository;
    private final MarketArticleCommentRepository marketArticleCommentRepository;
    private final ArticleImageService articleImageService;
    @Value("${file-server.domain}")
    private String domainUrl;

    /**
     * 장터 게시글 검색
     */
    @Transactional(readOnly = true)
    public Page<MarketArticleDto> searchMarketArticles(
            Pageable pageable,
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return marketArticleRepository.findAll(pageable)
                    .map(article ->
                            MarketArticleDto.from(
                                    article,
                                    getMarketArticleBookmarkCount(article),
                                    getMarketArticleCommentsCount(article),
                                    getMarketArticleImages(article)
                            )
                    );
        }

        return marketArticleRepository.findAllByTitleContainingOrContentContaining(
                        searchKeyword,
                        searchKeyword,
                        pageable
                )
                .map(article -> MarketArticleDto.from(
                        article,
                        getMarketArticleBookmarkCount(article),
                        getMarketArticleCommentsCount(article),
                        getMarketArticleImages(article)
                ));
    }

    @Transactional(readOnly = true)
    public Page<MarketArticleDto> searchMarketArticlesWithFetchJoin(
            Pageable pageable,
            String searchKeyword
    ) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return marketArticleRepository.findAllWithFetchJoin(pageable)
                    .map(article ->
                            MarketArticleDto.from(
                                    article,
                                    article.getLikes().size(),
                                    article.getComments().size(),
                                    getMarketArticleImages(article)
                            )
                    );
        }

        return marketArticleRepository.findAllByTitleContainingOrContentContainingByFetchJoin(
                        searchKeyword,
                        searchKeyword,
                        pageable
                )
                .map(article -> MarketArticleDto.from(
                        article,
                        article.getLikes().size(),
                        article.getComments().size(),
                        getMarketArticleImages(article)
                ));
    }

    /**
     * 장터 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public MarketArticleDto getMarketArticle(Long articleId) {
        MarketArticleEntity savedMarketArticle = getMarketArticleEntity(articleId);
        List<String> images = productImageRepository.findAllByMarketArticle(savedMarketArticle)
                .stream()
                .map(imageEntity -> domainUrl + imageEntity.getImageUrl())
                .collect(Collectors.toList());

        return MarketArticleDto.from(
                savedMarketArticle,
                getMarketArticleBookmarkCount(savedMarketArticle),
                getMarketArticleCommentsCount(savedMarketArticle),
                images
        );
    }

    /**
     * 장터 게시글 생성
     */
    public Long saveMarketArticle(
            CreateMarketArticleRequest request,
            UserDetails userPrincipal
    ) throws Exception {

        MarketArticleEntity savedMarketArticle = marketArticleRepository.save(request.toEntity(
                getMemberEntity(userPrincipal.getUsername())
        ));

        for (MultipartFile imageUrl : request.getImageUrls()) {
            ProductImageEntity productImage = ProductImageEntity.of(
                    articleImageService.uploadProfileImage(imageUrl),
                    savedMarketArticle
            );
            productImageRepository.save(productImage);
        }

        return savedMarketArticle.getId();
    }

    /**
     * 장터 게시글 수정
     */
    public void updateMarketArticle(
            Long articleId,
            UpdateMarketArticleRequest request,
            UserDetails userPrincipal
    ) throws Exception {

        MarketArticleEntity savedMarketArticle = getMarketArticleEntity(articleId);

        verifyResourceOwner(savedMarketArticle, getMemberEntity(userPrincipal.getUsername()));

        savedMarketArticle.update(
                request.getTitle(),
                MarketArticleStatus.of(request.getMarketArticleStatus()),
                request.getPrice(),
                ProductStatus.of(request.getProductStatus()),
                request.getContent()
        );

        productImageRepository.deleteAllByMarketArticle(savedMarketArticle);

        for (MultipartFile imageUrl : request.getImageUrls()) {
            ProductImageEntity productImage = ProductImageEntity.of(
                    articleImageService.uploadProfileImage(imageUrl),
                    savedMarketArticle
            );
            productImageRepository.save(productImage);
        }
    }

    /**
     * 장터 게시글 삭제
     */
    public void deleteMarketArticle(
            Long articleId,
            UserDetails userPrincipal
    ) {
        MarketArticleEntity savedMarketArticle = getMarketArticleEntity(articleId);

        verifyResourceOwner(savedMarketArticle, getMemberEntity(userPrincipal.getUsername()));

        productImageRepository.deleteAllByMarketArticle(savedMarketArticle);
        marketArticleCommentRepository.deleteAllByMarketArticle(savedMarketArticle);
        marketBookmarkRepository.deleteAllByMarketArticle(savedMarketArticle);

        marketArticleRepository.delete(savedMarketArticle);
    }

    public Optional<MarketBookmarkEntity> updateArticleLike(
            Long articleId,
            UserDetails principal
    ) {
        MarketArticleEntity marketArticle = getMarketArticleEntity(articleId);
        MemberEntity member = getMemberEntity(principal.getUsername());
        Optional<MarketBookmarkEntity> marketBookmark = marketBookmarkRepository.findByMarketArticleAndMemberEntity(marketArticle, member);
        if(marketBookmark.isPresent()){
            deleteBookmark(articleId, principal);
            return Optional.empty();
        } else{
            return Optional.of(createBookmark(articleId, principal));
        }
    }

    /**
     * 장터 게시글 북마크 등록
     *
     * @return
     */
    private MarketBookmarkEntity createBookmark(
            Long articleId,
            UserDetails principal
    ) {
        MarketArticleEntity marketArticle = getMarketArticleEntity(articleId);
        MemberEntity member = getMemberEntity(principal.getUsername());

        return marketBookmarkRepository.save(
                MarketBookmarkEntity.of(
                        marketArticle,
                        member
                )
        );
    }

    /**
     * 장터 게시글 북마크 삭제
     */
    private void deleteBookmark(
            Long articleId,
            UserDetails principal
    ) {
        marketBookmarkRepository.deleteAllByMarketArticleAndMemberEntity(
                getMarketArticleEntity(articleId),
                getMemberEntity(principal.getUsername())
        );
    }

    @Transactional(readOnly = true)
    public List<ArticleDto> getAllBookmarks( MemberEntity loginMemberEntity
    )  {
        List<MarketArticleEntity> marketArticles = marketBookmarkRepository.findAllByMemberEntity(
                        loginMemberEntity
                )
                .stream()
                .map(MarketBookmarkEntity::getMarketArticle)
                .collect(Collectors.toList());

        List<ArticleDto> results = new ArrayList<>();
        for (MarketArticleEntity marketArticle : marketArticles) {
            List<String> images = productImageRepository.findAllByMarketArticle(marketArticle)
                    .stream()
                    .map(imageEntity -> domainUrl + imageEntity.getImageUrl())
                    .collect(Collectors.toList());
            results.add(ArticleDto.from(
                    marketArticle,
                    getMarketArticleCommentsCount(marketArticle),
                    getMarketArticleBookmarkCount(marketArticle),
                    images
            ));
        }
        return results;
    }

    @Transactional(readOnly = true)
    public int getMarketArticleBookmarkCount(MarketArticleEntity marketArticle) {
        return marketBookmarkRepository.countAllByMarketArticle(marketArticle);
    }

    @Transactional(readOnly = true)
    public int getMarketArticleBookmarkCount(Long marketArticleId) {
        MarketArticleEntity marketArticle = marketArticleRepository.findById(marketArticleId).get();
        return marketBookmarkRepository.countAllByMarketArticle(marketArticle);
    }

    public MemberEntity getMemberEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    public MarketArticleEntity getMarketArticleEntity(Long marketArticleId) {
        return marketArticleRepository.findById(marketArticleId)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_NOT_FOUND));
    }

    public List<String> getMarketArticleImages(MarketArticleEntity marketArticle) {
        return productImageRepository.findAllByMarketArticle(marketArticle)
                .stream()
                .map(imageEntity -> domainUrl + imageEntity.getImageUrl())
                .collect(Collectors.toList());
    }

    public void verifyResourceOwner(
            MarketArticleEntity marketArticle,
            MemberEntity member
    ) {
        if (!marketArticle.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }

    public Integer getMarketArticleCommentsCount(MarketArticleEntity marketArticle) {
        return marketArticleCommentRepository.countAllByMarketArticle(marketArticle);
    }
}