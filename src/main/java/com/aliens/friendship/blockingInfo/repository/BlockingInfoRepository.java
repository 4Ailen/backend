package com.aliens.friendship.blockingInfo.repository;

import com.aliens.friendship.blockingInfo.domain.BlockingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockingInfoRepository extends JpaRepository<BlockingInfo, Integer> {
}