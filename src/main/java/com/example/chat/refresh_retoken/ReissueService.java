package com.example.chat.refresh_retoken;

import com.example.chat.config.CookieUtil;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.jwt.JwtUtil;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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

        String username = jwtUtil.getUsername(refresh_token);
        String role = jwtUtil.getRole(refresh_token);

        deleteRefreshToken(refresh_token);

        String new_jwt = jwtUtil.createJwt("access", username, role, 1800000L); // 3시간
        String new_refresh = jwtUtil.createJwt("refresh", username, role, 259200000L); // 3일

        log.info("새로운 Access 토큰: " + new_jwt);

        Cookie new_refresh_token = CookieUtil.createDefaultCookie("refresh_authorization", new_refresh);

        response.setHeader("authorization", new_jwt);
        response.addCookie(new_refresh_token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public void deleteRefreshToken(String refresh) {
        try {
            refreshTokenRepository.findByRefresh(refresh)
                    .ifPresentOrElse(refreshTokenRepository::delete,
                            () -> log.info("Refresh 토큰이 DB에 존재하지 않습니다."));

        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.warn("이미 삭제 되었습니다.");
            throw new ChatException(HttpStatus.CONFLICT, ErrorCode.OPTIMISTICLOCKING, "이미 삭제 되었습니다");
        }
    }
}
