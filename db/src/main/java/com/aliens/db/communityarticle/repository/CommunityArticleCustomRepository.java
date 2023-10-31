package com.aliens.db.communityarticle.repository;

import java.time.Instant;
import java.util.Optional;

public interface CommunityArticleCustomRepository {
    Optional<Instant> findLatestArticleTimeByMemberId(Long memberId);

}
