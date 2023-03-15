package com.aliens.friendship.matching.service;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.matching.domain.BlockingInfo;
import com.aliens.friendship.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockingInfoService {

    private final BlockingInfoRepository blockingInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void block(String email, int memberId){
        Member blockingMember =  memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Member blockedMember = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        BlockingInfo blockingInfo = BlockingInfo.builder().blockingMember(blockingMember).blockedMember(blockedMember).build();
        blockingInfoRepository.save(blockingInfo);
    }

    public List<BlockingInfo> findAllByBlockingMember(String email){
        Member blockingMember =  memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        List<BlockingInfo> blockingInfos = blockingInfoRepository.findAllByBlockingMember(blockingMember);
        return blockingInfos;
    }
}


