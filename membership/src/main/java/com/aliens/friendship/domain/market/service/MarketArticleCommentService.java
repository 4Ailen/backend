package com.aliens.friendship.domain.market.service;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.market.dto.MarketArticleCommentDto;
import com.aliens.friendship.domain.market.dto.request.CreateMarketArticleCommentRequest;
import com.aliens.friendship.domain.market.dto.request.UpdateMarketArticleCommentRequest;
import com.aliens.friendship.domain.market.entity.MarketArticleComment;
import com.aliens.friendship.domain.market.repository.MarketArticleCommentRepository;
import com.aliens.friendship.domain.market.repository.MarketArticleRepository;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.market.exception.MarketExceptionCode.MARKET_ARTICLE_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@RequiredArgsConstructor
@Service
public class MarketArticleCommentService {

    private final MarketArticleCommentRepository marketArticleCommentRepository;
    private final MarketArticleRepository marketArticleRepository;
    private final MemberRepository memberRepository;

    public List<MarketArticleCommentDto> getAllMarketArticleComment(
            Long marketArticleId
    ) {
        return marketArticleCommentRepository.findAllByMarketArticle_Id(marketArticleId)
                .stream()
                .map(MarketArticleCommentDto::from)
                .collect(Collectors.toList());
    }

    public Long createMarketArticleComment(
            Long marketArticleId,
            CreateMarketArticleCommentRequest request,
            UserDetails principal
    ) {
        return marketArticleCommentRepository.save(
                MarketArticleComment.of(
                        request.getContent(),
                        marketArticleRepository.findById(marketArticleId)
                                .orElseThrow(() -> new ResourceNotFoundException(MARKET_ARTICLE_NOT_FOUND)),
                        getmember(principal.getUsername())
                )
        ).getId();
    }

    public void updateMarketArticleComment(
            Long marketArticleCommentId,
            UpdateMarketArticleCommentRequest request,
            UserDetails principal
    ) {
        MarketArticleComment marketArticleComment = marketArticleCommentRepository.getReferenceById(marketArticleCommentId);

        verifyResourceOwner(marketArticleComment, getmember(principal.getUsername()));

        marketArticleComment.update(request.getContent());
    }

    public void deleteMarketArticleComment(
            Long marketArticleCommentId,
            UserDetails principal
    ) {

        MarketArticleComment marketArticleComment = marketArticleCommentRepository.getReferenceById(marketArticleCommentId);

        verifyResourceOwner(marketArticleComment, getmember(principal.getUsername()));

        marketArticleCommentRepository.delete(
                marketArticleComment
        );
    }

    private MemberEntity getmember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    private void verifyResourceOwner(
            MarketArticleComment marketArticleComment,
            MemberEntity member
    ) {
        if (!marketArticleComment.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }
}