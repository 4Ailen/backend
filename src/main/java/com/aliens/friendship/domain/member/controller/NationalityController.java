package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.member.service.NationalityService;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class NationalityController {

    private final NationalityService nationalityService;
    private final ResponseService responseService;

    @GetMapping("/nationalities")
    public SingleResult<Map<String, Object>> getNationalities() {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 국가 리스트를 조회하였습니다.",
                nationalityService.getNationalities()
        );
    }
}