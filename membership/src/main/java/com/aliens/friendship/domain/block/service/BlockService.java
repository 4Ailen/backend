package com.aliens.friendship.domain.block.service;

import com.aliens.db.block.entity.BlockEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.block.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;

    @Transactional
    public void block(MemberEntity blockedMemberEntity, MemberEntity blockingMemberEntity) {
        BlockEntity blockEntity = BlockEntity.builder().blockingMemberEntity(blockingMemberEntity).blockedMemberEntity(blockedMemberEntity).build();
        blockRepository.save(blockEntity);
    }


}