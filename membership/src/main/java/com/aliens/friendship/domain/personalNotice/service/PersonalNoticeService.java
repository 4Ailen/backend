package com.aliens.friendship.domain.personalNotice.service;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.db.personalNotice.entity.PersonalNoticeEntity;
import com.aliens.db.personalNotice.repository.PersonalNoticeRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.personalNotice.dto.PersonalNoticesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalNoticeService {
    private final PersonalNoticeRepository personalNoticeRepository;
    private final MemberService memberService;

    @Transactional
    public void markAsRead(Long personalNoticeId) {
        PersonalNoticeEntity personalNotice = personalNoticeRepository.findById(personalNoticeId).orElseThrow(() -> new NoSuchElementException("해당 알림을 찾을 수 없습니다."));
        personalNotice.updateIsRead(true);
    }

    public PersonalNoticesDto getPersonalNotices() throws Exception {
        List<PersonalNoticeEntity> personalNotices = personalNoticeRepository.findByReceiver(memberService.getCurrentMemberEntity());

        PersonalNoticesDto personalNoticesDto = new PersonalNoticesDto();

        for (PersonalNoticeEntity personalNotice : personalNotices) {
            MemberEntity member = personalNotice.getSender();
            PersonalNoticesDto.PersonalNoticeInfo personalNoticeInfo = PersonalNoticesDto.PersonalNoticeInfo.builder()
                    .noticeType(personalNotice.getNoticeType().toString())
                    .articleCategory(personalNotice.getArticleCategory().getValue())
                    .articleUrl(personalNotice.getArticleUrl())
                    .comment(personalNotice.getComment())
                    .name(member.getName())
                    .createdAt(personalNotice.getCreatedAt())
                    .isRead(personalNotice.getIsRead())
                    .nationality(member.getNationality())
                    .profileImage(member.getProfileImageUrl())
                    .personalNoticeId(personalNotice.getId())
                    .build();
            personalNoticesDto.getPersonalNoticeInfos().add(personalNoticeInfo);
        }

        return personalNoticesDto;
    }

}