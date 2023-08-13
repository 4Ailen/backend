package com.aliens.friendship.domain.market.service;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.market.dto.request.CreateMarketArticleRequest;
import com.aliens.friendship.domain.market.dto.request.MarketArticleDto;
import com.aliens.friendship.domain.market.dto.request.UpdateMarketArticleRequest;
import com.aliens.friendship.domain.market.entity.MarketArticle;
import com.aliens.friendship.domain.market.entity.MarketBookmark;
import com.aliens.friendship.domain.market.entity.ProductImage;
import com.aliens.friendship.domain.market.repository.MarketArticleRepository;
import com.aliens.friendship.domain.market.repository.MarketBookmarkRepository;
import com.aliens.friendship.domain.market.repository.ProductImageRepository;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.market.exception.MarketExceptionCode.MARKET_ARTICLE_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@RequiredArgsConstructor
@Service
public class MarketService {

    private final MarketArticleRepository marketArticleRepository;
    private final ProductImageRepository productImageRepository;
    private final MarketBookmarkRepository marketBookmarkRepository;
    private final MemberRepository memberRepository;

    public MarketArticleDto getMarketArticle(Long marketArticleId) {
        MarketArticle savedMarketArticle = getMarketArticleEntity(marketArticleId);
        List<String> images = productImageRepository.findAllByMarketArticle(savedMarketArticle)
                .stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return MarketArticleDto.from(
                savedMarketArticle,
                images
        );
    }

    public List<MarketArticleDto> getAllMarketArticles() {
        List<MarketArticle> marketArticles = marketArticleRepository.findAll();

        List<MarketArticleDto> marketArticleDtos = new ArrayList<>();
        for (MarketArticle marketArticle : marketArticles) {
            List<String> images = productImageRepository.findAllByMarketArticle(marketArticle).stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            marketArticleDtos.add(
                    MarketArticleDto.of(
                            marketArticle.getId(),
                            marketArticle.getTitle(),
                            marketArticle.getStatus(),
                            marketArticle.getPrice(),
                            marketArticle.getProductStatus(),
                            marketArticle.getContent(),
                            images
                    )
            );
        }

        return marketArticleDtos;
    }

    public Long saveMarketArticle(
            CreateMarketArticleRequest request,
            UserDetails userPrincipal
    ) {

        MarketArticle savedMarketArticle = marketArticleRepository.save(request.toEntity(
                getmember(userPrincipal.getUsername())
        ));

        for (String imageUrl : request.getImageUrls()) {
            ProductImage productImage = ProductImage.of(
                    imageUrl,
                    savedMarketArticle
            );
            productImageRepository.save(productImage);
        }

        return savedMarketArticle.getId();
    }

    public void updateMarketArticle(
            Long marketArticleId,
            UpdateMarketArticleRequest request,
            UserDetails userPrincipal
    ) {

        MarketArticle savedMarketArticle = getMarketArticleEntity(marketArticleId);

        verifyResourceOwner(savedMarketArticle, getmember(userPrincipal.getUsername()));

        savedMarketArticle.update(
                request.getTitle(),
                request.getStatus(),
                request.getPrice(),
                request.getProductStatus(),
                request.getContent()
        );

        productImageRepository.deleteAllByMarketArticle(savedMarketArticle);

        for (String imageUrl : request.getImageUrls()) {
            ProductImage productImage = ProductImage.of(
                    imageUrl,
                    savedMarketArticle
            );
            productImageRepository.save(productImage);
        }
    }

    public void deleteMarketArticle(
            Long marketArticleId,
            UserDetails userPrincipal
    ) {
        MarketArticle savedMarketArticle = getMarketArticleEntity(marketArticleId);

        verifyResourceOwner(savedMarketArticle, getmember(userPrincipal.getUsername()));

        productImageRepository.deleteAllByMarketArticle(savedMarketArticle);
        marketArticleRepository.delete(savedMarketArticle);
    }

    public void createBookmark(
            Long marketArticleId,
            UserDetails principal
    ) {
        MarketArticle marketArticle = getMarketArticleEntity(marketArticleId);
        MemberEntity member = getmember(principal.getUsername());

        marketBookmarkRepository.save(
                MarketBookmark.of(
                        marketArticle,
                        member
                )
        );
    }

    public void deleteBookmark(
            Long marketArticleId,
            UserDetails principal
    ) {
        marketBookmarkRepository.deleteAllByMarketArticleAndMemberEntity(
                getMarketArticleEntity(marketArticleId),
                getmember(principal.getUsername())
        );
    }

    private MemberEntity getmember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    private MarketArticle getMarketArticleEntity(Long marketArticleId) {
        return marketArticleRepository.findById(marketArticleId)
                .orElseThrow(() -> new ResourceNotFoundException(MARKET_ARTICLE_NOT_FOUND));
    }

    private void verifyResourceOwner(
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        if (!marketArticle.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }
}