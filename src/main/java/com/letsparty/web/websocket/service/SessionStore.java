package com.letsparty.web.websocket.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SessionStore {

	// WebSocketServerSockJsSession에서 세션객체를 저장하기 위한 Map.
	// k: sessionId, v: WebSocketServerSockJsSession
	private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
	
	//	WebSocket이 연결되면 Map에 추가한다.
	//		roomID가 들어오는 SUBSCRIBE 전 CONNECT 시점에 실행되므로 별도로 처리해야 한다.
	public void addSession(WebSocketSession session) {
		sessionMap.put(session.getId(), session);
	}
	
	//	WebSocket이 닫히면 Map에서 제거한다.
	//		구독해제 직전에 실행된다.
	public WebSocketSession removeSession(String id) {
		return sessionMap.remove(id);
	}
	
	public WebSocketSession getSession(String id) {
		return sessionMap.get(id);
	}
	
	public void closeSession(String id) {
		AbstractSockJsSession session = (AbstractSockJsSession) sessionMap.get(id);
		if (session != null && session.isActive()) {
			synchronized (session) {
				if (session.isActive()) {
					try {
						session.close();
					} catch (IOException e) {
						log.error("웹소켓세션 닫는 중 오류: {}, 예외: {}", id, e.getMessage());
					}
				}
			}
		}
	}
}
