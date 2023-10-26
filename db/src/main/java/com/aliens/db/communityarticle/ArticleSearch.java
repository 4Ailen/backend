package com.aliens.db.communityarticle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleSearch {
    Long searchWithAll();
    Page<ArticleDto>searchWithFetchJoin(Pageable page, String searchKeyword);
}
