package com.example.chat.jwt;


import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.Role;
import com.example.chat.oauth.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.equals("/api/reToken")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = null;

        Cookie[] cookies = request.getCookies();

        // 쿠키의 유무
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 access token을 추출
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("authorization")) {
                authorization = cookie.getValue();
            }
        }

        // access token 유무
        if (authorization == null) {
            log.info("token is null");
            filterChain.doFilter(request, response);

            return;
        }

        try {
            jwtUtil.isExpired(authorization);
        } catch (ChatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token이 만료되었습니다.");
            return;
        }

        String username = null;
        try {
            username = jwtUtil.getUsername(authorization);
        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
        }

        String role = jwtUtil.getRole(authorization);

        Member member = Member.builder()
                .username(username)
                .role(Role.valueOf(role))
                .build();

        Map<String, Object> attributes = new HashMap<>();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(member, attributes);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
