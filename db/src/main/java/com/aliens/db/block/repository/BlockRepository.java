package com.aliens.db.block.repository;

import com.aliens.db.block.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<BlockEntity, Integer> {
}