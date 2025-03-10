package com.example.chat.refresh_retoken;

import com.example.chat.config.CookieUtil;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public ResponseEntity<?> reissue(String refreshAuthorization, HttpServletRequest request, HttpServletResponse response) {

        String refresh_token = refreshAuthorization.substring(7);

        try {
            jwtUtil.isExpired(refresh_token);
        } catch (ChatException e){
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESHTOKEN_IS_EXPIRED);
        }

        String category = jwtUtil.getCategory(refresh_token);

        if (!category.equals("refresh")) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.IS_NOT_REFRESHTOKEN);
        }

        String username = null;

        try {
            username = jwtUtil.getUsername(refresh_token);
        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
        }

        String role = jwtUtil.getRole(refresh_token);

        String new_jwt = jwtUtil.createJwt("access", username, role, 1800000L); // 30분, 1800000L
        String new_refresh = jwtUtil.createJwt("refresh", username, role, 259200000L); // 3일

        redisTemplate.opsForValue().set(
                "refresh_token:" + username,
                new_refresh,
                259200000L,
                TimeUnit.MILLISECONDS
        );

        log.info("새로운 Access 토큰: " + new_jwt);

        Cookie new_refresh_token = CookieUtil.createDefaultCookie("refresh_authorization", new_refresh);

        response.setHeader("authorization", new_jwt);
        response.addCookie(new_refresh_token);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
