package com.example.chat.member;

import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.jwt.JwtUtil;
import com.example.chat.member.dto.MemberResDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MemberResDto info(String token) {

        try {
            jwtUtil.isExpired(token);

            String username = jwtUtil.getUsername(token);

            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

            return MemberResDto.from(member);

        } catch (ChatException e) {
            throw e;
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

    @Transactional
    public void editNickName(Long id, String editNickName) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER)
                );

        member.setNickName(editNickName);
    }


    public List<ChatRoomResDto> memberChatRooms(Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER);
        }

        return chatRoomRepository.findChatroomsByMemberId(memberId);
    }
}
