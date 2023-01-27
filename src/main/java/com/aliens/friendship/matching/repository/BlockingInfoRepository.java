package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.BlockingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockingInfoRepository extends JpaRepository<BlockingInfo, Integer> {
}