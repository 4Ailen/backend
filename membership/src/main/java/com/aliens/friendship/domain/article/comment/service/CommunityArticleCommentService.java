package com.aliens.friendship.domain.article.comment.service;

import com.aliens.db.communityarticle.entity.CommunityArticleEntity;
import com.aliens.db.communityarticle.repository.CommunityArticleRepository;
import com.aliens.db.communityarticlecomment.entity.CommunityArticleCommentEntity;
import com.aliens.db.communityarticlecomment.repository.CommunityArticleCommentRepository;
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
import static com.aliens.friendship.domain.article.comment.exception.ArticleCommentExceptionCode.ARTICLE_COMMENT_NOT_FOUND;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@Transactional
@RequiredArgsConstructor
@Service
public class CommunityArticleCommentService {

    private final CommunityArticleRepository communityArticleRepository;
    private final CommunityArticleCommentRepository communityArticleCommentRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentsDto> getAllArticleComments(
            Long articleId
    ) {
        List<CommunityArticleCommentEntity> communityArticleComments = communityArticleCommentRepository.findAllByCommunityArticle_Id(articleId);

        List<ArticleCommentsDto> result = new ArrayList<>();
        for (CommunityArticleCommentEntity comment : communityArticleComments) {
            if (comment.getType() == PARENT) {
                result.add(
                        ArticleCommentsDto.from(
                                ArticleCommentDto.from(comment),
                                communityArticleCommentRepository.findAllByParentCommentId(comment.getId())
                                        .stream()
                                        .map(ArticleCommentDto::from)
                                        .collect(Collectors.toList())
                        )
                );
            }
        }

        return result;
    }

    public CommunityArticleCommentEntity createCommunityArticleComment(
            Long articleId,
            CreateArticleCommentRequest request,
            UserDetails principal
    ) {
        return communityArticleCommentRepository.save(
                CommunityArticleCommentEntity.of(
                        request.getContent(),
                        PARENT,
                        getCommunityArticleEntity(articleId),
                        null,
                        getMemberEntity(principal.getUsername())
                )
        );
    }

    public CommunityArticleCommentEntity createCommunityArticleCommentReply(
            Long commentId,
            CreateArticleCommentRequest request,
            UserDetails principal
    ) {
        return communityArticleCommentRepository.save(
                CommunityArticleCommentEntity.of(
                        request.getContent(),
                        CHILD,
                        getCommunityArticleCommentEntity(commentId).getCommunityArticle(),
                        commentId,
                        getMemberEntity(principal.getUsername())
                )
        );
    }

    public void updateCommunityArticleComment(
            Long commentId,
            UpdateArticleCommentRequest request,
            UserDetails principal
    ) {
        CommunityArticleCommentEntity communityArticleComment = communityArticleCommentRepository.getReferenceById(commentId);

        verifyResourceOwner(communityArticleComment, getMemberEntity(principal.getUsername()));

        communityArticleComment.update(request.getContent());
    }

    public void deleteCommunityArticleComment(
            Long commentId,
            UserDetails principal
    ) {

        CommunityArticleCommentEntity communityArticleComment = communityArticleCommentRepository.getReferenceById(commentId);

        verifyResourceOwner(communityArticleComment, getMemberEntity(principal.getUsername()));

        communityArticleComment.deleteComment();
    }

    private CommunityArticleEntity getCommunityArticleEntity(Long articleId) {
        return communityArticleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_COMMENT_NOT_FOUND));
    }

    private MemberEntity getMemberEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    private void verifyResourceOwner(
            CommunityArticleCommentEntity communityArticleComment,
            MemberEntity member
    ) {
        if (!communityArticleComment.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }

    private CommunityArticleCommentEntity getCommunityArticleCommentEntity(Long id) {
        return communityArticleCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_COMMENT_NOT_FOUND));
    }
}