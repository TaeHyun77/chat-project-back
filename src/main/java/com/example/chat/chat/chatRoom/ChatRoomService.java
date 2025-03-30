package com.example.chat.chat.chatRoom;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 채팅방 생성
    public void createChatRoom(ChatRoomRequestDto dto) {

        Member member = memberRepository.findByUsername(dto.getCreator())
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(dto.getChatRoomName())
                .build();

        chatRoom.setMember(member);

        chatRoomRepository.save(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoom> selectAllChatRoom() {
        return chatRoomRepository.getChatRooms();
    }

    // 특정 채팅방 정보 조회
    public ChatRoomResDto chatRoomInfo(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithChatsAndMember(roomId);

        if (chatRoom == null) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM);
        }

        return ChatRoomResDto.builder()
                .id((chatRoom.getId()))
                .chatRoomName(chatRoom.getChatRoomName())
                .member(chatRoom.getMember())
                .chats(chatRoom.getChats())
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }

    public ResponseEntity<?> deleteRoom(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithChatsAndMember(roomId);

        if (chatRoom == null) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM);
        }

        try {
            chatRoomRepository.delete(chatRoom);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }
    }
}
