package com.aliens.friendship.member.controller;


import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.service.NationalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/member")
public class NationalityController {

    NationalityService nationalityService;

    @GetMapping("/nationalities")
    public Response<Map<String, Object>> getNationalities() {
        return Response.SUCCESS(nationalityService.getNationalities());
    }
}
