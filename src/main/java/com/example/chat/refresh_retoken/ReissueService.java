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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<?> reissue(String refreshAuthorization, HttpServletResponse response) {

        String refresh_token = refreshAuthorization.substring(7);

        validateRefreshToken(refresh_token);

        String username = jwtUtil.getUsername(refresh_token);
        String role = jwtUtil.getRole(refresh_token);

        String new_jwt = jwtUtil.createJwt("access", username, role, 1800000L); // 30분, 1800000L
        String new_refresh = jwtUtil.createJwt("refresh", username, role, 259200000L); // 3일

        saveRefreshToken(username, new_refresh);

        log.info("새로운 AccessToken: {}", new_jwt);

        Cookie new_refresh_token = CookieUtil.createDefaultCookie("refresh_authorization", new_refresh);

        response.setHeader("authorization", new_jwt);
        response.addCookie(new_refresh_token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateRefreshToken(String refreshToken) {
        if (jwtUtil.isExpired(refreshToken)) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESHTOKEN_IS_EXPIRED);
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.IS_NOT_REFRESHTOKEN);
        }
    }

    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {

        LocalDateTime expiredAt = LocalDateTime.now().plusDays(3);

        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .refresh(refreshToken)
                .expiration(expiredAt)
                .build();

        refreshTokenRepository.save(refresh);
    }
}
