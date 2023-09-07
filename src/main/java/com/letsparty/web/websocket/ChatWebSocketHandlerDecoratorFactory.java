package com.letsparty.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import com.letsparty.web.websocket.service.SessionStore;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

	private final SessionStore sessionStore;

	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
        return new ChatWebSocketHandlerDecorator(handler, sessionStore);
	}
}
