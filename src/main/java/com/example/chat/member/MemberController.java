package com.example.chat.member;

import com.example.chat.airport.plane.dto.PlaneResDto;
import com.example.chat.auth.oauth.CustomOAuth2User;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.messaging.chatRoom.dto.ChatRoomResDto;
import com.example.chat.member.dto.MemberResDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Value("${app.base-url}")
    private String baseUrl;

    // 로그인 사용자 정보
    @GetMapping("/info")
    public MemberResDto memberInfo(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            throw new ChatException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
        }
        return memberService.info(user.getUsername());
    }

    // 닉네임 중복 여부 파악
    @GetMapping("/isNickName/{editNickName}")
    public boolean isNickName(@PathVariable("editNickName") String editNickName) {

        log.info("editNickName : {}", editNickName);

        return memberService.isNickName(editNickName);
    }

    // 닉네임 수정
    @PostMapping("/edit/{id}/{editNickName}")
    public void editUsername(@PathVariable("id") Long id, @PathVariable("editNickName") String editNickName) {

        log.info("id : {} , editNickName : {}", id, editNickName);
        memberService.editNickName(id, editNickName);
    }

    @GetMapping("/member/{id}/chatRooms")
    public List<ChatRoomResDto> memberChatRooms(@PathVariable("id") Long memberId) {
        return memberService.memberChatRooms(memberId);
    }

    @GetMapping("/googleLogin")
    public ResponseEntity<?> googleLogin(HttpServletResponse response) {
        log.info("Login request success");
        String redirectUrl = baseUrl + "/oauth2/authorization/google";

        return ResponseEntity.ok().body(Map.of("url", redirectUrl));
    }

    @PostMapping("/googleLogout")
    public ResponseEntity<String> googleLogout(HttpServletRequest request, HttpServletResponse response) {
        return memberService.googleLogout(request, response);
    }

    // 항공편 구독
    @PostMapping("/flight/subscribe")
    public ResponseEntity<Void> subscribePlane(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Long> body) {
        memberService.subscribePlane(userDetails.getUsername(), body.get("planeId"));
        return ResponseEntity.ok().build();
    }

    // 항공편 구독 해제
    @DeleteMapping("/flight/subscribe/{planeId}")
    public ResponseEntity<Void> unsubscribePlane(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long planeId) {
        memberService.unsubscribePlane(userDetails.getUsername(), planeId);
        return ResponseEntity.noContent().build();
    }

    // 구독 항공편 목록 조회
    @GetMapping("/flight/subscriptions")
    public ResponseEntity<List<PlaneResDto>> getSubscribedPlanes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.getSubscribedPlanes(userDetails.getUsername()));
    }
}
