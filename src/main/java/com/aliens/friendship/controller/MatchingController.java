package com.aliens.friendship.controller;

import com.aliens.friendship.domain.Language;
import com.aliens.friendship.domain.Question;
import com.aliens.friendship.dto.ApplicantInfo;
import com.aliens.friendship.dto.MatchedApplicants;
import com.aliens.friendship.service.MatchingService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    @GetMapping("/matching/question")
    public Question getQuestion() {
        return matchingService.chooseQuestion();
    }

    @PostMapping("/matching/applicant")
    public void applyMatching(@RequestBody ApplicantInfo applicantInfo) {
        matchingService.applyMatching(applicantInfo);
    }

    @GetMapping("/matching/status")
    public JSONObject getStatus() throws ParseException {
        String str = "{\"status\" : \"" + matchingService.checkStatus() + "\"}";
        JSONParser parser = new JSONParser();
        JSONObject status = (JSONObject) parser.parse(str);

        return status;
    }
    @GetMapping("/matching/result")
    public List<MatchedApplicants> matchingTeams() {
        return matchingService.teamBuilding();
    }

}
