package com.aliens.friendship.member.service;

import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NationalityService {

    private final NationalityRepository nationalityRepository;

    public Map<String, Object> getNationalities() {
        List<Nationality> Nationalities = Optional.ofNullable(nationalityRepository.findAll()).orElseThrow(() -> new NoSuchElementException("국적 목록이 데이터베이스에 없습니다."));
        Map<String, Object> result = new HashMap<>();
        result.put("nationalities", Nationalities);
        return result;
    }
}
