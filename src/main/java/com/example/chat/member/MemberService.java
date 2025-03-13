package com.example.chat.member;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public ResponseEntity<?> Info(String token) {

        try {

            if (jwtUtil.isExpired(token)) {
                throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
            }

            String username = null;
            try {
                username = jwtUtil.getUsername(token);
            } catch (ChatException e) {
                throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
            }

            String role = jwtUtil.getRole(token);

            Optional<Member> member = memberRepository.findByUsername(username);

            String name = null;
            String email = null;

            if (member.isPresent()) {
                name = member.get().getName();
                email = member.get().getEmail();
            }

            MemberResponseDto memberInfo = MemberResponseDto.builder()
                    .username(username)
                    .name(name)
                    .email(email)
                    .role(Role.valueOf(role))
                    .build();

            return new ResponseEntity<>(memberInfo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> googleLogout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("로그 아웃 성공 !");

        return ResponseEntity.ok("로그 아웃 성공");
    }


}
