package com.web_socket;

import com.mapper.ShoppingMallMapper;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.service.ShoppingMallService;
import com.web_socket.WebSocketMessage.Type;
import com.web_socket.dto.ShoppingMallUpdateRequestDto;
import io.quarkus.logging.Log;
import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@WebSocket(path = "/quarkus-demo-api/ws/shopping-mall")
@RequiredArgsConstructor
public class ShoppingMallWebSocket {

    private final WebSocketConnection connection;

    private final ShoppingMallService shoppingMallService;

    private final ShoppingMallMapper mallMapper;

    @OnOpen
    public void onOpen() {
        Log.info("Connection opened! Connection id: " + connection.id());
    }

    @OnTextMessage
    public Uni<WebSocketMessage> onMessage(WebSocketMessage message) {
        switch (message.type()) {
            case GET -> {
                return shoppingMallService.getShoppingMallById(message.getMessageAs(Integer.class))
                    .map(mall -> new WebSocketMessage(Type.GET, mall));
            }
            case GET_LIST -> {
                return shoppingMallService.getAllShoppingMalls()
                    .map(mallList -> new WebSocketMessage(Type.GET_LIST, mallList));
            }
            case CREATE -> {
                return shoppingMallService.createShoppingMall(message.getMessageAs(ShoppingMallCreateRequest.class),
                        connection.id())
                    .map(mall -> new WebSocketMessage(Type.CREATE, mall));
            }
            case UPDATE -> {
                ShoppingMallUpdateRequestDto dto = message.getMessageAs(ShoppingMallUpdateRequestDto.class);
                return shoppingMallService.updateShoppingMall(dto.getId(), mallMapper.toShoppingMallUpdateRequest(dto),
                        connection.id())
                    .map(empty -> new WebSocketMessage(Type.UPDATE, "Shopping mall updated."));
            }
            case DELETE -> {
                return shoppingMallService.deleteShoppingMall(message.getMessageAs(Integer.class), connection.id())
                    .map(empty -> new WebSocketMessage(Type.DELETE, "Shopping mall deleted."));
            }
            default -> {
                return Uni.createFrom().item(new WebSocketMessage(Type.UNRECOGNIZED, "Message type not supported"));
            }
        }
    }

    @OnClose
    public void onClose(CloseReason reason) {
        Log.info("Connection closed! " + reason + ". Connection id: " + connection.id());
    }
}
