package com.aliens.friendship.matching.service.model;

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
    private List<Participant> matchingList;

    public Participant(int id, String firstPreferLanguage, String secondPreferLanguage) {
        this.id = id;
        this.firstPreferLanguage = firstPreferLanguage;
        this.secondPreferLanguage = secondPreferLanguage;
        this.matchingList = new ArrayList<>();
    }

    public void addToMatchingList(Participant participant) {
        matchingList.add(participant);
    }

    public int getNumberOfMatches() {
        return matchingList.size();
    }

    public String toString() {
        return "참가자 " + id;
    }

}
