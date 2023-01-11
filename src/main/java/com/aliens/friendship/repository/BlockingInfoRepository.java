package com.aliens.friendship.repository;

import com.aliens.friendship.domain.BlockingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockingInfoRepository extends JpaRepository<BlockingInfo, Integer> {
}