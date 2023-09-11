package com.letsparty.web.websocket.interceptor;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.web.websocket.service.SessionInfoMapper;
import com.letsparty.web.websocket.service.SessionInfoMapper.SessionDetail;
import com.letsparty.web.websocket.util.WebSocketUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
//public class ChatChannelInterceptor implements ChannelInterceptor, ApplicationListener<SessionSubscribeEvent> {
public class ChatChannelInterceptor implements ChannelInterceptor {

	private final SessionInfoMapper sessionInfoMapper;
	private final ChatUserMapper chatUserMapper;

	@SuppressWarnings({ "null", "incomplete-switch" })
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
//		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		final String sessionId = accessor.getSessionId();
		
	    switch (accessor.getCommand()) {
        case SEND:
            if (accessor.getDestination().startsWith("/topic")) {
                throw new AccessDeniedException("topic에 메시지를 보낼 권한이 없음");
            }
            log.info("ws발송 message: {}", message);
            
            // 메시지의 목적지가 정상 참여 중인 방인지 검증 
            String roomId = WebSocketUtils.getLastVariableFromDestination(accessor.getDestination());
            SessionDetail sessionDetail = sessionInfoMapper.getSessionDetail(sessionId);
            if (!roomId.equals(sessionDetail.getRoomId())) {
				throw new AccessDeniedException("허가되지 않은 메시지 발송");
			}
            log.info("roomId: {}, sessionId: {}", roomId, sessionId);
            break;
            
        case CONNECT:
            if (accessor.getUser() == null) {
                // TODO: 서비스 이용에 권한이 필요하게 되면 수정 필요
                log.info("ChatChannel 접속 거부된 accessor: {}", accessor);
                throw new AccessDeniedException("로그인하지 않음");
            }
            log.info("ws접속: {}", accessor);
            break;
            
        case DISCONNECT:
        	sessionDetail = sessionInfoMapper.removeSession(sessionId);
            if (sessionDetail != null) {
            	int userNo = sessionDetail.getUserNo();
            	// 해당 유저의 채팅방 접속이 남아있지 않다면 마지막읽은메시지번호를 저장
            	if (sessionInfoMapper.getSessionIdsOfUserInRoom(userNo, sessionDetail.getRoomId()).isEmpty()) {
            		chatUserMapper.updateLastReadMessageNoById(sessionDetail.getRoomId(), userNo);
            	}
                log.info("ws구독해제 -> 방:{}, 유저:{}", sessionDetail.getRoomId(), userNo);
            }
            break;
		}
		
		return message;
	}
}
