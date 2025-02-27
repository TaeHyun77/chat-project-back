package com.example.chat.oauth;

import com.example.chat.config.CookieUtil;
import com.example.chat.jwt.JwtUtil;
import com.example.chat.refresh_retoken.RefreshToken;
import com.example.chat.refresh_retoken.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access_token = jwtUtil.createJwt("access", username, role, 1800000L); // 30분
        String refresh_token = jwtUtil.createJwt("refresh", username, role, 259200000L); // 3일

        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .refresh(refresh_token)
                .expiration((new Date(System.currentTimeMillis() + 60*60*10000L)).toString())
                .build();

        refreshTokenRepository.save(refresh);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Cookie access_cookie = CookieUtil.createDefaultCookie("authorization", access_token);
        Cookie refresh_cookie = CookieUtil.createDefaultCookie("refresh_authorization", refresh_token);

        response.setHeader("authorization", access_token);
        response.addCookie(access_cookie);
        response.addCookie(refresh_cookie);

        Date expirationTime = jwtUtil.getExpiration(access_token);
        Date reExpirationTime = jwtUtil.getExpiration(refresh_token);

        log.info("Access 토큰이 발급 되었습니다.");
        log.info("Access 토큰 만료 시간: " + expirationTime);
        log.info("refresh 토큰 만료 시간: " + reExpirationTime);

        response.sendRedirect("http://localhost:3000/");
    }

}
