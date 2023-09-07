package com.letsparty.web.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import com.letsparty.web.websocket.service.SessionStore;

public class ChatWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

	private final SessionStore sessionStore;
	
	public ChatWebSocketHandlerDecorator(WebSocketHandler delegate, SessionStore sessionStore) {
		super(delegate);
		this.sessionStore = sessionStore;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionStore.addSession(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessionStore.removeSession(session.getId());
		super.afterConnectionClosed(session, closeStatus);
	}
}
