package com.aliens.friendship.domain.notice.service;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.db.notice.entity.NoticeEntity;
import com.aliens.db.notice.repository.NoticeRepository;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.notice.dto.NoticeDto;
import com.aliens.friendship.domain.notice.dto.request.CreateNoticeRequest;
import com.aliens.friendship.domain.notice.dto.request.UpdateNoticeRequest;
import com.aliens.friendship.global.error.InvalidResourceOwnerException;
import com.aliens.friendship.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static com.aliens.friendship.domain.notice.exception.NoticeExceptionCode.NOTICE_NOT_FOUND;
import static com.aliens.friendship.global.error.GlobalExceptionCode.INVALID_RESOURCE_OWNER;

@Transactional
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<NoticeDto> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoticeDto getNotice(Long noticeId) {
        return NoticeDto.from(
                getNoticeEntity(noticeId)
        );
    }

    public Long createNotice(
            CreateNoticeRequest request,
            UserPrincipal principal
    ) {

        return noticeRepository.save(
                NoticeEntity.of(
                        request.getTitle(),
                        request.getContent(),
                        getMemberEntity(principal.getUsername())
                )
        ).getId();
    }

    public void updateNotice(
            Long noticeId,
            UpdateNoticeRequest request,
            UserPrincipal principal
    ) {
        NoticeEntity savedNoticeEntity = noticeRepository.getReferenceById(noticeId);

        verifyResourceOwner(savedNoticeEntity, getMemberEntity(principal.getUsername()));

        savedNoticeEntity.update(
                request.getTitle(),
                request.getContent()
        );
    }

    public void deleteNotice(
            Long noticeId,
            UserPrincipal principal
    ) {
        NoticeEntity savedNoticeEntity = noticeRepository.getReferenceById(noticeId);

        verifyResourceOwner(savedNoticeEntity, getMemberEntity(principal.getUsername()));

        noticeRepository.delete(savedNoticeEntity);
    }

    private NoticeEntity getNoticeEntity(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException(NOTICE_NOT_FOUND));
    }

    private MemberEntity getMemberEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }

    private void verifyResourceOwner(
            NoticeEntity noticeEntity,
            MemberEntity member
    ) {
        if (!noticeEntity.getMember().getId().equals(member.getId())) {
            throw new InvalidResourceOwnerException(INVALID_RESOURCE_OWNER);
        }
    }
}