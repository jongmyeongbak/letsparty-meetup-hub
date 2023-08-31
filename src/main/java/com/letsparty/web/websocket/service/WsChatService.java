package com.letsparty.web.websocket.service;

import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.ChatMessageMapper;
import com.letsparty.web.websocket.dto.ChatMessageDto;
import com.letsparty.web.websocket.service.SessionInfoMapper.SessionDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WsChatService {

	private final SessionInfoMapper sessionInfoMapper;
	private final ChatMessageMapper chatMessageMapper;

	public ChatMessageDto handleSendMessage(ChatMessageDto message, String roomId, Map<String, Object> headers) {
		String simpSessionId = (String) headers.get("simpSessionId");
		SessionDetail sessionDetail = sessionInfoMapper.getSessionDetail(simpSessionId);
		// 메시지 목적지가 변조된 경우
		if (!roomId.equals(sessionDetail.getRoomId())) {
			throw new AccessDeniedException("허용되지 않은 메시지");
		}
		message.setUserNo(sessionDetail.getUserNo());
		// TODO 시간도 담아 DB에 채팅메시지 삽입하기 
		return message;
	}
}
