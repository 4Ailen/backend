package com.aliens.friendship.domain.block.controller;

import com.aliens.friendship.domain.block.business.BlockBusiness;
import com.aliens.friendship.global.response.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/block")
@RequiredArgsConstructor
@Slf4j
public class BlockController {

    private  final BlockBusiness blockBusiness;

    @PostMapping("/{memberId}")
    public ResponseEntity<CommonResult> blocking(
            @PathVariable Long memberId,
            @RequestBody Map<String, String> roomId
            ) throws Exception {
        blockBusiness.block(memberId, Long.valueOf(roomId.get("roomId")));
        return ResponseEntity.ok(
                CommonResult.of(
                        "차단 완료"
                )
        );
    }

}
