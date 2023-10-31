package com.aliens.friendship.domain.article.market.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

@SpringBootTest
class MarketArticleServiceTest {

    @Autowired
    private MarketArticleService marketArticleService;

    @Test
    @Transactional
    public void testSearchMarketArticles() {
        String searchKeyword = "";
        Pageable pageable = PageRequest.of(0, 10); // Set the page and size as needed

        long startTime = System.currentTimeMillis();
        marketArticleService.searchMarketArticles(pageable, searchKeyword);
        long endTime = System.currentTimeMillis();

        System.out.println("Search time: " + (endTime - startTime) + "ms");

        // Perform assertions on the result as needed
    }

    @Test
    @Transactional
    public void testSearchMarketArticlesWithFetchJoin() {
        String searchKeyword = "";
        Pageable pageable = PageRequest.of(0, 10); // Set the page and size as needed

        long startTime = System.currentTimeMillis();
        marketArticleService.searchMarketArticlesWithFetchJoin(pageable, searchKeyword);
        long endTime = System.currentTimeMillis();

        System.out.println("Search with fetch join time: " + (endTime - startTime) + "ms");

        // Perform assertions on the result as needed
    }
}