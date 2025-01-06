package com.web_socket;

import io.quarkus.logging.Log;
import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@WebSocket(path = "/quarkus-demo-api/ws")
public class ApiWebSocket {

    @Inject
    WebSocketConnection connection;

    @OnOpen
    public void onOpen() {
        Log.info("Connection opened! Connection id: " + connection.id());
    }

    @OnTextMessage
    public Uni<WebSocketMessage> onMessage(WebSocketMessage message) {
        return Uni.createFrom().item(new WebSocketMessage("Message received: " + message.message()));
    }

    @OnClose
    public void onClose(CloseReason reason) {
        Log.info("Connection closed! " + reason + ". Connection id: " + connection.id());
    }
}
