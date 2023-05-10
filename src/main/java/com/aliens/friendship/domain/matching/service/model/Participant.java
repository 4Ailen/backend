package com.aliens.friendship.domain.matching.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Participant {
    private int id;
    private String firstPreferLanguage;
    private String secondPreferLanguage;
    private List<Matching> matchingList;

    public Participant(int id, String firstPreferLanguage, String secondPreferLanguage) {
        this.id = id;
        this.firstPreferLanguage = firstPreferLanguage;
        this.secondPreferLanguage = secondPreferLanguage;
        this.matchingList = new ArrayList<>();
    }

    public void addToMatchingList(Matching matching) {
        matchingList.add(matching);
    }

    public int getNumberOfMatches() {
        return matchingList.size();
    }

    public String toString() {
        return "참가자 " + id;
    }
}
