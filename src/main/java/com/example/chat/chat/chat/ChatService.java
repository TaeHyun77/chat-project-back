package com.example.chat.chat.chat;

import com.example.chat.chat.chat.dto.ChatReqDto;
import com.example.chat.chat.chat.dto.ChatResDto;
import com.example.chat.chat.chat.repository.ChatRepository;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import com.example.chat.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
    * 발행된 채팅을 처리하는 메서드
    * 일반 채팅 메세지와, 입퇴장 메세지를 따로 처리
    * 채팅을 DB에 저장하고, 해당 채팅방으로 전송
    *
    * ❌ 트랜잭션 범위 안에서 API 호출하는 문제가 있긴함, 다른 빈으로 빼고 사용해야함❌
    * */
    @Transactional
    public void pushChat(ChatReqDto dto) {

        Member member = memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        saveChat(dto, member);

    }

    // 메시지 타입에 따른 처리 분리
    private void saveChat(ChatReqDto dto, Member member) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(dto.getChatroomId())
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM));

        // 입/퇴장 메세지는 따로 처리
        if (dto.getChatType() == ChatType.ENTER) {
            dto.setContent(member.getName() + ":enter");
        } else if (dto.getChatType() == ChatType.EXIT) {
            dto.setContent(member.getName() + ":exit");
        }

        Chat chat = dto.toEntity(chatRoom, member);
        chatRepository.save(chat);

        sendChat(dto.getChatroomId(), chat);
    }

    // 클라이언트에서 특정 채팅방에 발송된 메세지를 해당 채팅방에 전송
    private void sendChat(String chatroomId, Chat chat) {

        ChatResDto chatMessage = ChatResDto.from(chat);

        messagingTemplate.convertAndSend("/topic/chat/" + chatroomId, chatMessage);
    }

    /*
    * 특정 채팅방의 모든 채팅 조회
    * */
    public List<ChatResDto> getChatListByChatroom(String chatRoomId) {

        List<Chat> chats = chatRepository.getChatsByChatRoomId(chatRoomId);

        return chats.stream()
                .map(ChatResDto::from)
                .toList();
    }
}