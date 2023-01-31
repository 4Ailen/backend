package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public Response<Map<String,Object>> join(JoinDto joinDto) throws Exception {

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profileImagePath", memberService.join(joinDto));

        return Response.SUCCESS(responseData);
    }


}
