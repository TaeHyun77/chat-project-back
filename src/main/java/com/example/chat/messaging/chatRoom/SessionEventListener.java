package com.example.chat.messaging.chatRoom;

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SessionEventListener implements ApplicationListener<ApplicationEvent> {

    // 세션 ID -> 채팅방 ID 매핑
    private final ConcurrentMap<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    // 채팅방 ID -> 접속 유저 수 (AtomicInteger로 원자적 증감 보장)
    private final ConcurrentMap<String, AtomicInteger> roomUserCountMap = new ConcurrentHashMap<>();
    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final SimpMessageSendingOperations messagingTemplate;

    public SessionEventListener(WebSocketHandler webSocketHandler, SimpMessageSendingOperations messagingTemplate) {
        this.subProtocolWebSocketHandler = (SubProtocolWebSocketHandler) webSocketHandler;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectedEvent || event instanceof SessionDisconnectEvent) {
            int allUserCnt = subProtocolWebSocketHandler.getStats().getWebSocketSessions();
            messagingTemplate.convertAndSend("/topic/all/userCnt", allUserCnt);
        }

        if (event instanceof SessionSubscribeEvent subscribeEvent) {
            handleSubscription(subscribeEvent);
        } else if (event instanceof SessionDisconnectEvent disconnectEvent) {
            handleDisconnect(disconnectEvent);
        }
    }

    // 채팅방들에 존재하는 모든 사용자의 수
    public int getAllUserCnt() {
        return subProtocolWebSocketHandler.getStats().getWebSocketSessions();
    }

    // 특정 채팅방에 존재하는 사용자의 수
    public int getChatRoomUserCnt(String roomId) {
        AtomicInteger count = roomUserCountMap.get(roomId);
        return count != null ? count.get() : 0;
    }

    // 사용자가 특정 채팅방을 구독할 때 실행
    private void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        if (destination == null || !destination.startsWith("/topic/chatroom/userCnt/")) {
            return;
        }

        String roomId = destination.replace("/topic/chatroom/userCnt/", "");

        sessionRoomMap.put(sessionId, roomId);
        int count = roomUserCountMap.computeIfAbsent(roomId, k -> new AtomicInteger(0)).incrementAndGet();

        messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, count);
    }

    // 사용자가 WebSocket 연결을 끊을 때 실행
    private void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        String roomId = sessionRoomMap.remove(sessionId);
        if (roomId == null) {
            return;
        }

        AtomicInteger count = roomUserCountMap.get(roomId);
        if (count == null) {
            return;
        }

        int newCount = count.decrementAndGet();
        if (newCount <= 0) {
            roomUserCountMap.remove(roomId);
        }

        messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, Math.max(newCount, 0));
    }
}
