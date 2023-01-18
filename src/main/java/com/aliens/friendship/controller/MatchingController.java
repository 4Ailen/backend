package com.aliens.friendship.controller;

import com.aliens.friendship.domain.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MatchingController {

    private final MatchingService matchingService;

    @Autowired
    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/matching/languages")
    public List<Language> getLanguages() {
        return matchingService.getLanguages();
    }
}
