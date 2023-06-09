package englishdictionary.server.component;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage("Received message: " + message.getPayload()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
