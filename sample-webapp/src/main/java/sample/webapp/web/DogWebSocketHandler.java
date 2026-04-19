package sample.webapp.web;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

class DogWebSocketHandler extends TextWebSocketHandler {
    private final AtomicInteger greetingsCounter = new AtomicInteger(0);
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        sendGreetingsCount(session, greetingsCounter.get());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        if ("inc".equals(message.getPayload())) {
            int greetingsCount = greetingsCounter.incrementAndGet();
            for (WebSocketSession s : sessions)
                sendGreetingsCount(s, greetingsCount);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private static void sendGreetingsCount(WebSocketSession session, int greetingsCount) throws IOException {
        session.sendMessage(new TextMessage(String.valueOf(greetingsCount)));
    }
}
