package com.aliens.friendship.member.controller;


import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.service.NationalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class NationalityController {

    private final NationalityService nationalityService;

    @GetMapping("/nationalities")
    public Response<Map<String, Object>> getNationalities() {
        return Response.SUCCESS(nationalityService.getNationalities());
    }
}
