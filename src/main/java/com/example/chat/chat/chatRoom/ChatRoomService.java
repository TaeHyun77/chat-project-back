package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chatRoom.dto.ChatRoomReqDto;
import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import com.example.chat.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // 채팅방 삭제
    @Transactional
    public void deleteChatroom(String chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatroomId)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM));

        chatRoomRepository.delete(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoomResDto> getAllChatroom() {

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRooms();

        return chatRooms.stream()
                .map(ChatRoomResDto::from)
                .collect(Collectors.toList());
    }

    // 특정 채팅방 조회
    public ChatRoomResDto getChatroomInfo(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM));

        return ChatRoomResDto.from(chatRoom);
    }
}
