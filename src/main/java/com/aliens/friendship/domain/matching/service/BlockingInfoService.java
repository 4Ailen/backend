package com.aliens.friendship.domain.matching.service;

import com.aliens.friendship.domain.matching.domain.BlockingInfo;
import com.aliens.friendship.domain.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockingInfoService {

    private final BlockingInfoRepository blockingInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void block(int memberId) {
        String email = getCurrentMemberEmail();
        Member blockingMember = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Member blockedMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        BlockingInfo blockingInfo = BlockingInfo.builder().blockingMember(blockingMember).blockedMember(blockedMember).build();
        blockingInfoRepository.save(blockingInfo);
    }

    public List<BlockingInfo> findAllByBlockingMember(String email) {
        Member blockingMember = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<BlockingInfo> blockingInfos = blockingInfoRepository.findAllByBlockingMember(blockingMember);
        return blockingInfos;
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}