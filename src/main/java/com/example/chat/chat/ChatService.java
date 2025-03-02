package com.example.chat.chat;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.jwt.JwtUtil;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    public void pushMessage(MessageRequestDto requestDto) {

        String token = requestDto.getAccessToken();

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        token = token.substring(7);

        if (jwtUtil.isExpired(token)) {
            throw new IllegalArgumentException("Token expired");
        }

        String username = jwtUtil.getUsername(token);

        Optional<Member> member = memberRepository.findByUsername(username);

        if (member.isEmpty()) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER);
        } else {

            if (requestDto.getMessageType() == MessageType.ENTER) {
                requestDto.setContent(member.get().getName() + "님이 입장하였습니다.");
            }

            MessageResponseDto sendMessage = MessageResponseDto.builder()
                    .content(requestDto.getContent())
                    .username(username)
                    .name(member.get().getName())
                    .email(member.get().getEmail())
                    .timestamp(requestDto.getTimestamp())
                    .roomId(requestDto.getRoomId())
                    .build();

            messagingTemplate.convertAndSend("/topic/chat/" + requestDto.getRoomId(), sendMessage);
        }
    }
}
