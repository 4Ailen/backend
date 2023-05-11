package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.BlockingInfo;
import com.aliens.friendship.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockingInfoRepository extends JpaRepository<BlockingInfo, Integer> {
    List<BlockingInfo> findAllByBlockingMember(Member blockingMember);

}