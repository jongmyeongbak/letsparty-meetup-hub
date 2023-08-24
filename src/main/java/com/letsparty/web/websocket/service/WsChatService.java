package com.letsparty.web.websocket.service;

import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.letsparty.web.websocket.dto.ChatMessageDto;
import com.letsparty.web.websocket.service.SessionInfoMapper.SessionDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WsChatService {

	private final SessionInfoMapper sessionInfoMapper;

	public ChatMessageDto handleSendMessage(ChatMessageDto message, String roomId, Map<String, Object> headers) {
		String simpSessionId = (String) headers.get("simpSessionId");
		SessionDetails sessionDetails = sessionInfoMapper.getSessionDetails(simpSessionId);
		 // 메시지 목적지가 변조된 경우
		if (!roomId.equals(sessionDetails.getRoomId())) {
			throw new AccessDeniedException("허용되지 않은 메시지");
		}
		message.setUserNo(sessionDetails.getUserNo());
//		message.setUserNo(((LoginUser) ((Authentication) principal).getPrincipal()).getNo());
		return message;
	}
	
//	public boolean isUserAllowedInRoom(String userId, String roomId) {
//		ChatRoom chatRoom = chatRoomMapper.
//	}
}
