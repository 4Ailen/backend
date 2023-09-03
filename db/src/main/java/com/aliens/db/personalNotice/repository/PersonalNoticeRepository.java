package com.aliens.db.personalNotice.repository;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.personalNotice.entity.PersonalNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalNoticeRepository extends JpaRepository<PersonalNoticeEntity, Long> {
    List<PersonalNoticeEntity> findByReceiver(MemberEntity memberEntity);
}
