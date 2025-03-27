package com.example.chat.refresh_retoken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReissueController {

    private final ReissueService reissueService;

    // access 토큰 재발급 로직
    @PostMapping("/api/reToken")
    public ResponseEntity<?> reissueController(@RequestHeader("refreshAuthorization") String refreshAuthorization, HttpServletRequest request, HttpServletResponse response) {

        log.info("access token 재발급 시도 중 ..");
        return reissueService.reissue(refreshAuthorization, request, response);
    }

}
