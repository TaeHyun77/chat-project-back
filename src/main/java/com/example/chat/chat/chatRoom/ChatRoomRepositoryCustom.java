package com.example.chat.chat.chatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> getChatRooms();

    ChatRoom findChatRoomWithChatsAndMember(String chatRoomId);

}
