package com.example.chat.config;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Component
public class SessionEventListener implements ApplicationListener<SessionConnectedEvent> {

    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public SessionEventListener(WebSocketHandler webSocketHandler, SimpMessageSendingOperations simpMessageSendingOperations) {
        this.subProtocolWebSocketHandler = (SubProtocolWebSocketHandler) webSocketHandler;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        int webSocketSessions = subProtocolWebSocketHandler.getStats().getWebSocketSessions();
        System.out.println("[DEBUG] 현재 웹소켓 세션 수: " + webSocketSessions);
        simpMessageSendingOperations.convertAndSend("/topic/number",webSocketSessions);
    }
}
