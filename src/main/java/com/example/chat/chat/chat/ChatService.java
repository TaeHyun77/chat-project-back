package com.example.chat.chat.chat;

import com.example.chat.chat.chat.dto.ChatReqDto;
import com.example.chat.chat.chat.dto.ChatResDto;
import com.example.chat.chat.chat.repository.ChatRepository;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.repository.MemberRepository;
import com.example.chat.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    public List<ChatResDto> getAllChats(String chatRoomId) {

        List<Chat> chats = chatRepository.getChats(chatRoomId);

        return chats.stream()
                .map(chat -> ChatResDto.builder()
                        .chatType(chat.getChatType())
                        .content(chat.getContent())
                        .member(MemberResDto.fromMemberEntity(chat.getMember()))
                        .createdAt(chat.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

    }

    public void pushMessage(ChatReqDto requestDto) {

        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        handleMessageByType(requestDto, member);
    }

    // 메시지 타입에 따른 처리 로직 분리
    private void handleMessageByType(ChatReqDto requestDto, Member member) {
        if (requestDto.getChatType() == ChatType.TALK) {
            handleTalkMessage(requestDto, member);
        } else if (requestDto.getChatType() == ChatType.ENTER || requestDto.getChatType() == ChatType.EXIT) {
            handleEnterAndExitMessage(requestDto, member);
        } else {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_MESSAGE_TYPE);
        }
    }

    // 입장, 퇴장 메시지 처리
    private void handleEnterAndExitMessage(ChatReqDto requestDto, Member member) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(requestDto.getRoomId());

        if (chatRoom == null) {
            throw new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM);
        }

        if (requestDto.getChatType() == ChatType.ENTER) {
            requestDto.setContent(member.getName() + "님이 입장하였습니다.");
        } else if (requestDto.getChatType() == ChatType.EXIT) {
            requestDto.setContent(member.getName() + "님이 퇴장하였습니다.");
        }

        Chat chat = Chat.builder()
                .content(requestDto.getContent())
                .chatRoom(chatRoom)
                .chatType(requestDto.getChatType())
                .member(member)
                .build();

        chatRepository.save(chat);

        messagingTemplate.convertAndSend("/topic/chat/" + requestDto.getRoomId(), requestDto);
    }

    // 일반 채팅 메시지 처리
    private void handleTalkMessage(ChatReqDto requestDto, Member member) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(requestDto.getRoomId());

        if (chatRoom == null) {
            throw new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM);
        }

        // 채팅 저장
        Chat chat = Chat.builder()
                .content(requestDto.getContent())
                .chatRoom(chatRoom)
                .chatType(requestDto.getChatType())
                .member(member)
                .build();

        chatRepository.save(chat);

        // 클라이언트에서 발송된 메세지 전송
        ChatResDto sendMessage = ChatResDto.builder()
                .chatType(requestDto.getChatType())
                .content(requestDto.getContent())
                .member(MemberResDto.fromMemberEntity(member))
                .createdAt(requestDto.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + requestDto.getRoomId(), sendMessage);
    }
}