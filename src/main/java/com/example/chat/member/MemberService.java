package com.example.chat.member;

import com.example.chat.chat.chatRoom.ChatRoomMemberDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public MemberResDto Info(String token) {

        try {
            try {
                jwtUtil.isExpired(token);
            } catch (ChatException e) {
                throw new ChatException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
            }

            String username = null;

            try {
                username = jwtUtil.getUsername(token);
            } catch (ChatException e) {
                throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
            }

            String role = jwtUtil.getRole(token);

            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

            return MemberResDto.builder()
                    .id(member.getId())
                    .username(member.getUsername())
                    .name(member.getName())
                    .email(member.getEmail())
                    .role(Role.valueOf(role))
                    .nickName(member.getNickName())
                    .build();

        } catch (Exception e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_RESPONSE_MEMBER);
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

    public boolean isNickName(String editNickName) {

        return memberRepository.existsByNickName(editNickName);

    }

    public ResponseEntity<?> editNickName(Long id, String editNickName) {

        Optional<Member> member = memberRepository.findById(id);

        if (member.isPresent()) {
            member.get().setNickName(editNickName);
            memberRepository.save(member.get());
        } else {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public MemberResDto memberChatRooms(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        return MemberResDto.of(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getEmail(),
                member.getNickName(),
                member.getRole(),
                member.getChatRooms().stream()
                        .map(ChatRoomMemberDto::from)
                        .toList()
        );
    }

    public void deleteAllMember() {
        memberRepository.deleteAll();
    }
}
