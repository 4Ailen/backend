package com.aliens.friendship.domain.match.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Participant {
    private Long id;
    private String firstPreferLanguage;
    private String secondPreferLanguage;
    private List<ServiceModelMatching> serviceModelMatchingList;

    public Participant(Long id, String firstPreferLanguage, String secondPreferLanguage) {
        this.id = id;
        this.firstPreferLanguage = firstPreferLanguage;
        this.secondPreferLanguage = secondPreferLanguage;
        this.serviceModelMatchingList = new ArrayList<>();
    }

    public void addToMatchingList(ServiceModelMatching serviceModelMatching) {
        serviceModelMatchingList.add(serviceModelMatching);
    }

    public int getNumberOfMatches() {
        return serviceModelMatchingList.size();
    }

    public String toString() {
        return "참가자 " + id;
    }
}
