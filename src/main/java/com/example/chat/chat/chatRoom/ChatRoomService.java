package com.example.chat.chat.chatRoom;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        chatRoom.selectChatRoomMembers(member);

        chatRoomRepository.save(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoom> selectAllChatRoom() {
        return chatRoomRepository.findAll();
    }
}
