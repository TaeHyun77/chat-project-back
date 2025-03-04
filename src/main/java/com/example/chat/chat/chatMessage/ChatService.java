package com.example.chat.chat.chatMessage;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.ChatRoomRepository;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    public void pushMessage(ChatRequestDto requestDto) {

        String username = validateAndExtractUsername(requestDto.getAccessToken());

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        handleMessageByType(requestDto, member);
    }

    // 토큰 검증 및 사용자 이름 추출
    private String validateAndExtractUsername(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        token = token.substring(7);

        if (jwtUtil.isExpired(token)) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
        }

        try {
            return jwtUtil.getUsername(token);
        } catch (Exception e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ACCESSTOKEN_IS_EXPIRED);
        }
    }

    // 메시지 타입에 따른 처리 로직 분리
    private void handleMessageByType(ChatRequestDto requestDto, Member member) {
        if (requestDto.getChatType() == ChatType.ENTER) {
            handleEnterMessage(requestDto, member);
        } else if (requestDto.getChatType() == ChatType.TALK) {
            handleTalkMessage(requestDto, member);
        } else {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_MESSAGE_TYPE);
        }
    }

    // 입장 메시지 처리
    private void handleEnterMessage(ChatRequestDto requestDto, Member member) {
        requestDto.setContent(member.getName() + "님이 입장하였습니다.");
        messagingTemplate.convertAndSend("/topic/chat/" + requestDto.getRoomId(), requestDto);
    }

    // 일반 채팅 메시지 처리
    private void handleTalkMessage(ChatRequestDto requestDto, Member member) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(requestDto.getRoomId());

        if (chatRoom == null) {
            throw new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM);
        }

        Chat chat = Chat.builder()
                .content(requestDto.getContent())
                .chatRoom(chatRoom)
                .chatType(requestDto.getChatType())
                .username(member.getUsername())
                .build();

        chatRepository.save(chat);

        ChatResponseDto sendMessage = ChatResponseDto.builder()
                .chatType(requestDto.getChatType())
                .content(requestDto.getContent())
                .username(member.getUsername())
                .name(member.getName())
                .email(member.getEmail())
                .timestamp(requestDto.getTimestamp())
                .roomId(requestDto.getRoomId())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + requestDto.getRoomId(), sendMessage);
    }
}