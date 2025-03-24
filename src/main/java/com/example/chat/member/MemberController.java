package com.example.chat.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<?> memberInfo(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        String token = authorizationHeader.substring(7);

        return memberService.Info(token);
    }

    @GetMapping("/roomCreatorInfo/{roomCreator}")
    public String roomCreatorInfo(@PathVariable("roomCreator") String roomCreator) {

        return memberService.roomCreatorInfo(roomCreator);

    }

    // 닉네임 중복 여부 파악
    @GetMapping("/isNickName/{editNickName}")
    public boolean isNickName(@PathVariable("editNickName") String editNickName) {

        log.info("editNickName : " + editNickName);

        return memberService.isNickName(editNickName);

    }

    // 닉네임 수정
    @PostMapping("/edit/{id}/{editNickName}")
    public ResponseEntity<?> editUsername(@PathVariable("id") Long id, @PathVariable("editNickName") String editNickName) {

        log.info("id : " + id + " , " + "editNickName : " + editNickName);
        return memberService.editNickName(id, editNickName);

    }

    @GetMapping("/googleLogin")
    public ResponseEntity<?> googleLogin(HttpServletResponse response) {
        log.info("Login request success");
        String redirectUrl = "http://localhost:8080/oauth2/authorization/google"; // 로컬용

//        String redirectUrl = "https://incheon-airport-info.site/oauth2/authorization/google"; // AWS 도메인 적용

        return ResponseEntity.ok().body(Map.of("url", redirectUrl));
    }

    @PostMapping("/googleLogout")
    public ResponseEntity<String> googleLogout(HttpServletRequest request, HttpServletResponse response) {

        return memberService.googleLogout(request, response);

    }

    @DeleteMapping("/deleteAll")
    public void deleteAllMember() {
        memberService.deleteAllMember();
    }
}
