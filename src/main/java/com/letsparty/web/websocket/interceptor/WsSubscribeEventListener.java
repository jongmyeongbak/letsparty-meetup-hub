package com.letsparty.web.websocket.interceptor;

import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.ChatUser;
import com.letsparty.web.websocket.service.SessionInfoMapper;
import com.letsparty.web.websocket.util.WebSocketUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WsSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

	private final SessionInfoMapper sessionInfoMapper;
	private final ChatUserMapper chatUserMapper;

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		Message<byte[]> message = event.getMessage();
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		String roomId = WebSocketUtils.getLastVariableFromDestination(accessor.getDestination());
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		log.info("sessionAttributes -> {}", sessionAttributes);

		if (sessionAttributes == null || !roomId.equals(sessionAttributes.get("roomId"))) {
			throw new AccessDeniedException("허가되지 않은 구독 요청");
		}

		int userNo = ((LoginUser) ((Authentication) accessor.getUser()).getPrincipal()).getNo();
		// 참가한 room이 아니면 구독 거부
		if (!isUserInRoom(roomId, userNo)) {
			throw new AccessDeniedException("topic을 구독할 권한이 없음");
		}
		
		sessionInfoMapper.addSession(accessor.getSessionId(), roomId, userNo);
		log.info("ws구독 -> 방:{}, 유저:{}", roomId, userNo);
	}

	private boolean isUserInRoom(String roomId, int userNo) {
		ChatUser chatUser = ChatUser.builder().roomId(roomId).userNo(userNo).build();
		Long lastReadMessageNo = chatUserMapper.findLastReadMessageNoByRoomNoAndUserNo(chatUser);
		return lastReadMessageNo != null;
	}
}