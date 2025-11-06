package com.example.chat.chat.chatRoom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Slf4j
@Component
public class SessionEventListener implements ApplicationListener<ApplicationEvent> {

    private final ConcurrentMap<String, String> sessionRoomMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> roomUserCountMap = new ConcurrentHashMap<>();
    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final SimpMessageSendingOperations messagingTemplate;

    private String chatRoomId = null;
    private int chatRoomUserCnt = 0;
    private int allUserCnt = 0;

    public SessionEventListener(WebSocketHandler webSocketHandler, SimpMessageSendingOperations messagingTemplate) {
        this.subProtocolWebSocketHandler = (SubProtocolWebSocketHandler) webSocketHandler;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectedEvent || event instanceof SessionDisconnectEvent) {
            allUserCnt = subProtocolWebSocketHandler.getStats().getWebSocketSessions();
        }

        messagingTemplate.convertAndSend("/topic/all/userCnt", allUserCnt);

        if (event instanceof SessionSubscribeEvent) {
            handleSubscription((SessionSubscribeEvent) event);
        } else if (event instanceof SessionDisconnectEvent) {
            handleDisconnect((SessionDisconnectEvent) event);
        }
    }

    // 사용자가 특정 채팅방을 구독할 때 실행
    private void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination(); // 사용자의 구독 경로

        String roomId = Objects.requireNonNull(destination).replace("/topic/chatroom/userCnt/", "");

        if (destination.startsWith("/topic/chatroom/userCnt/")) {

            sessionRoomMap.put(sessionId, roomId); // 세션 ID -> 채팅방 ID 매핑
            roomUserCountMap.merge(roomId, 1, Integer::sum); // 채팅방 유저 수 증가

            chatRoomId = roomId;
            chatRoomUserCnt = roomUserCountMap.get(roomId);

            log.info("[DEBUG] 채팅방 {} 유저 수: {}", roomId, roomUserCountMap.get(roomId));

            messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, chatRoomUserCnt);

        }
    }

    // 사용자가 특정 채팅방을 구독 해제할 때 실행
    private void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionRoomMap.containsKey(sessionId)) {
            String roomId = sessionRoomMap.remove(sessionId);

            roomUserCountMap.computeIfPresent(roomId, (key, count) -> count > 1 ? count - 1 : null);

            chatRoomUserCnt = roomUserCountMap.getOrDefault(roomId, 0);

            log.info("[DEBUG] 채팅방 {} 유저 수: {}", roomId, roomUserCountMap.getOrDefault(roomId, 0));

            messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, chatRoomUserCnt);
        }
    }
}
