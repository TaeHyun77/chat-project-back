package com.example.chat.messaging.chat;

import com.example.chat.messaging.chat.dto.ChatReqDto;
import com.example.chat.messaging.chat.dto.ChatResDto;
import com.example.chat.messaging.chat.repository.ChatRepository;
import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.messaging.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    * */
    @Transactional
    public void receiveChat(ChatReqDto dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(dto.getChatroomId())
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM));

        Chat chat = dto.toEntity(chatRoom, member);
        chatRepository.save(chat);

        sendChat(dto.getChatroomId(), chat);
    }

    // 클라이언트에서 특정 채팅방에 발송된 메세지를 해당 채팅방에 전송
    private void sendChat(String chatroomId, Chat chat) {
        ChatResDto chatMessage = ChatResDto.from(chat);

        messagingTemplate.convertAndSend("/topic/chat/" + chatroomId, chatMessage);
    }

    // 특정 채팅방의 모든 채팅 조회
    public List<ChatResDto> getChatListByChatroom(String chatRoomId) {
        List<Chat> chats = chatRepository.getChatsByChatRoomId(chatRoomId);

        return chats.stream()
                .map(ChatResDto::from)
                .toList();
    }
}