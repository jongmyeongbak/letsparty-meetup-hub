package com.letsparty.web.websocket.interceptor;

import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.letsparty.mapper.ChatMessageMapper;
import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.ChatUser;
import com.letsparty.web.websocket.dto.ChatMessageCon;
import com.letsparty.web.websocket.service.SessionInfoMapper;
import com.letsparty.web.websocket.util.WebSocketUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WsSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

	private final SimpMessagingTemplate messagingTemplate;
	private final SessionInfoMapper sessionInfoMapper;
	private final ChatUserMapper chatUserMapper;
	private final ChatMessageMapper chatMessageMapper;

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

		// 방에 접속하면 안읽은수 감소
		if (!sessionInfoMapper.isUserInRoom(userNo, roomId)) {
			Long lastReadMessageNo = chatUserMapper
					.findLastReadMessageNoByRoomNoAndUserNo(ChatUser.builder().roomId(roomId).userNo(userNo).build());
			ChatMessageCon chatMessageCon = new ChatMessageCon(3, lastReadMessageNo);
			messagingTemplate.convertAndSend(String.format("/topic/chat/%s", roomId), chatMessageCon);
			chatMessageMapper.decreaseUnreadCntByRoomIdAndLastReadMessageNo(roomId, lastReadMessageNo);
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