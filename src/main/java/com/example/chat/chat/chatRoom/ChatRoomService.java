package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chatRoom.dto.ChatRoomReqDto;
import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.repository.MemberRepository;
import com.example.chat.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void createChatRoom(ChatRoomReqDto chatRoomReqDto) {

        Member member = memberRepository.findByUsername(chatRoomReqDto.getCreator())
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomReqDto.toChatRoom();
        chatRoom.setMember(member);

        chatRoomRepository.save(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoomResDto> selectAllChatRoom() {

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRooms();

        return chatRooms.stream()
                .map(chatRoom -> ChatRoomResDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .chatRoomName(chatRoom.getChatRoomName())
                        .member(MemberResDto.fromMemberEntity(chatRoom.getMember()))
                        .createdAt(chatRoom.getCreatedAt())
                        .modifiedAt(chatRoom.getModifiedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 특정 채팅방 정보 조회
    public ChatRoomResDto chatRoomInfo(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithMember(roomId)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM));

        return ChatRoomResDto.builder()
                .chatRoomId(roomId)
                .chatRoomName(chatRoom.getChatRoomName())
                .member(MemberResDto.fromMemberEntity(chatRoom.getMember()))
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }

    @Transactional
    public void deleteRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithMember(roomId)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CHATROOM));

        chatRoomRepository.delete(chatRoom);
    }
}
