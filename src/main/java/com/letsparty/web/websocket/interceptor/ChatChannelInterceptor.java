package com.letsparty.web.websocket.interceptor;

import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.ChatUser;
import com.letsparty.web.websocket.service.SessionInfoMapper;
import com.letsparty.web.websocket.service.SessionInfoMapper.SessionDetails;
import com.letsparty.web.websocket.util.WebSocketUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class ChatChannelInterceptor implements ChannelInterceptor {

	private final SessionInfoMapper sessionInfoMapper;
	private final ChatUserMapper chatUserMapper;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
//		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//		String sessionId = (String) message.getHeaders().get("simpSessionId");
		final String sessionId = accessor.getSessionId();
		
	    switch (accessor.getCommand()) {
        case SEND:
            if (accessor.getDestination().startsWith("/topic")) {
                throw new AccessDeniedException("topic에 메시지를 보낼 권한이 없음");
            }
            log.info("발송 message: {}", message);
            log.info("발송 message headers: {}", message.getHeaders());
            // TODO: destination의 roomId로 찾은 room에 sessionId가 있는지 검증 후 메시지에 추가
            String roomId = WebSocketUtils.getLastVariableFromDestination(accessor.getDestination());
            SessionDetails sessionDetails = sessionInfoMapper.getSessionDetails(sessionId);
            log.info("simpMessageType: {}", accessor.getMessageHeaders().get("simpMessageType"));
            break;
            
        case CONNECT:
            if (accessor.getUser() == null) {
                // TODO: 서비스 이용에 권한이 필요하게 되면 수정 필요
                log.info("ChatChannel 접속 거부된 accessor: {}", accessor);
                throw new AccessDeniedException("로그인하지 않음");
            }
            log.info("접속: {}", accessor);
            break;
            
        case SUBSCRIBE:
            roomId = WebSocketUtils.getLastVariableFromDestination(accessor.getDestination());
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            log.info("sessionAttributes -> {}", sessionAttributes);
            log.info("sessionAttributes.roomId -> {}", sessionAttributes.get("roomId"));
            
            if (sessionAttributes == null || !roomId.equals(sessionAttributes.get("roomId"))) {
                throw new AccessDeniedException("허가되지 않은 구독 요청");
            }
            
            int userNo = ((LoginUser) ((Authentication) accessor.getUser()).getPrincipal()).getNo();
            // 참가한 room이 아니면 구독 거부
            if (!isUserInRoom(roomId, userNo)) {
            	// TODO: 개발 중 Bypass 주석 해제
                // throw new AccessDeniedException("topic을 구독할 권한이 없음");
            }
            sessionInfoMapper.addSession(sessionId, roomId, userNo);
            log.info("구독 -> 방:{}, 유저:{}", roomId, userNo);
            break;
            
        case DISCONNECT:
        	sessionDetails = sessionInfoMapper.removeSession(sessionId);
            // 개발 확인용 코드
            if (sessionDetails != null) {
                userNo = ((LoginUser) ((Authentication) accessor.getUser()).getPrincipal()).getNo();
                log.info("구독해제 -> 방:{}, 유저:{}", sessionDetails.getRoomId(), userNo);
            }
            break;
		}
		
		return message;
	}
	
	public boolean isUserInRoom(String roomId, int userNo) {
		ChatUser chatUser = ChatUser.builder().roomId(roomId).userNo(userNo).build();
		Long lastReadMessageNo = chatUserMapper.findLastReadMessageNoByRoomNoAndUserNo(chatUser);
		return lastReadMessageNo != null;
	}
}
