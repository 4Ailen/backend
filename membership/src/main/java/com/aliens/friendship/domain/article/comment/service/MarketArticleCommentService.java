package com.aliens.friendship.domain.article.comment.service;

import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.marketarticle.repository.MarketArticleRepository;
import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.db.marketarticlecomment.repository.MarketArticleCommentRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.article.comment.dto.ArticleCommentDto;
import com.aliens.friendship.domain.article.comment.dto.ArticleCommentsDto;
import com.aliens.friendship.domain.article.comment.dto.CreateArticleCommentRequest;
import com.aliens.friendship.domain.article.comment.dto.UpdateArticleCommentRequest;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aliens.db.marketarticlecomment.CommentType.CHILD;
import static com.aliens.db.marketarticlecomment.CommentType.PARENT;
import static com.aliens.friendship.domain.article.exception.ArticleExceptionCode.ARTICLE_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@Transactional
@RequiredArgsConstructor
@Service
public class MarketArticleCommentService {

    private final MarketArticleCommentRepository marketArticleCommentRepository;
    private final MarketArticleRepository marketArticleRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentsDto> getAllMarketArticleComments(
            Long articleId
    ) {
        List<MarketArticleCommentEntity> marketArticleComments = marketArticleCommentRepository.findAllByMarketArticle_Id(articleId);

        List<ArticleCommentsDto> result = new ArrayList<>();
        for (MarketArticleCommentEntity comment : marketArticleComments) {
            if (comment.getType() == PARENT) {
                result.add(
                        ArticleCommentsDto.from(
                                ArticleCommentDto.from(comment),
                                marketArticleCommentRepository.findAllByParentCommentId(comment.getId())
                                        .stream()
                                        .map(ArticleCommentDto::from)
                                        .collect(Collectors.toList())
                        )
                );
            }
        }

        return result;
    }

    public MarketArticleCommentEntity createMarketArticleComment(
            Long articleId,
            CreateArticleCommentRequest request,
            UserDetails principal
    ) {
        return marketArticleCommentRepository.save(
                MarketArticleCommentEntity.of(
                        request.getContent(),
                        PARENT,
                        getMarketArticleEntity(articleId),
                        null,
                        getMemberEntity(principal.getUsername())
                )
        );
    }

    public MarketArticleCommentEntity createMarketArticleCommentReply(
            Long articleId,
            Long commentId,
            CreateArticleCommentRequest request,
            UserDetails principal
    ) {
        return marketArticleCommentRepository.save(
                MarketArticleCommentEntity.of(
                        request.getContent(),
                        CHILD,
                        getMarketArticleEntity(articleId),
                        commentId,
                        getMemberEntity(principal.getUsername())
                )
        );
    }

    public void updateMarketArticleComment(
            Long commentId,
            UpdateArticleCommentRequest request,
            UserDetails principal
    ) {
        MarketArticleCommentEntity marketArticleComment = marketArticleCommentRepository.getReferenceById(commentId);

        verifyResourceOwner(marketArticleComment, getMemberEntity(principal.getUsername()));

        marketArticleComment.update(request.getContent());
    }

    public void deleteMarketArticleComment(
            Long commentId,
            UserDetails principal
    ) {

        MarketArticleCommentEntity marketArticleComment = marketArticleCommentRepository.getReferenceById(commentId);

        verifyResourceOwner(marketArticleComment, getMemberEntity(principal.getUsername()));

        marketArticleComment.deleteComment();
    }

    private MarketArticleEntity getMarketArticleEntity(Long articleId) {
        return marketArticleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_NOT_FOUND));
    }

    private MemberEntity getMemberEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    private void verifyResourceOwner(
            MarketArticleCommentEntity marketArticleComment,
            MemberEntity member
    ) {
        if (!marketArticleComment.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }
}