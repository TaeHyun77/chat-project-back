package com.example.chat.messaging.chatRoom;

import com.example.chat.messaging.chatRoom.dto.ChatRoomReqDto;
import com.example.chat.messaging.chatRoom.dto.ChatRoomResDto;
import com.example.chat.messaging.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.messaging.member.Member;
import com.example.chat.messaging.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 채팅방 생성
    @Transactional
    public void createChatRoom(ChatRoomReqDto dto) {
        Member member = memberRepository.findByUsername(dto.getCreator())
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = dto.toEntity(member);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 삭제 (방 생성자만 가능)
    @Transactional
    public void deleteChatroom(String chatroomId, String username) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatroomId)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CHATROOM));

        if (!chatRoom.getMember().getUsername().equals(username)) {
            throw new ChatException(HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        chatRoomRepository.delete(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoomResDto> getAllChatroom() {
        List<ChatRoom> chatRooms = chatRoomRepository.getChatRooms();

        return chatRooms.stream()
                .map(ChatRoomResDto::from)
                .toList();
    }

    // 특정 채팅방 조회
    public ChatRoomResDto getChatroomInfo(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM));

        return ChatRoomResDto.from(chatRoom);
    }
}
