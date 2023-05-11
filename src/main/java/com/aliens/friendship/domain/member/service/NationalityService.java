package com.aliens.friendship.domain.member.service;

import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.exception.NationalitiesNotFoundException;
import com.aliens.friendship.domain.member.repository.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NationalityService {

    private final NationalityRepository nationalityRepository;

    public Map<String, Object> getNationalities() {
        List<Nationality> Nationalities = Optional.ofNullable(nationalityRepository.findAll()).orElseThrow(NationalitiesNotFoundException::new);
        Map<String, Object> result = new HashMap<>();
        result.put("nationalities", Nationalities);
        return result;
    }
}