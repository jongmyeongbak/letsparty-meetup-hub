package com.letsparty.web.websocket.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.ChatMessageMapper;
import com.letsparty.mapper.ChatRoomMapper;
import com.letsparty.vo.ChatMessage;
import com.letsparty.vo.ChatRoom;
import com.letsparty.web.websocket.service.SessionInfoMapper.SessionDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WsChatService {

	private final SimpMessagingTemplate messagingTemplate;
	private final SessionInfoMapper sessionInfoMapper;
	private final ChatRoomMapper chatRoomMapper;
	private final ChatMessageMapper chatMessageMapper;

	public void handleSendMessage(String text, String roomId, Map<String, Object> headers) {
		if (text == null || text.isBlank()) {
			return;
		}

		String simpSessionId = (String) headers.get("simpSessionId");
		SessionDetail sessionDetail = sessionInfoMapper.getSessionDetail(simpSessionId);
		// 메시지 목적지가 변조된 경우
		if (!roomId.equals(sessionDetail.getRoomId())) {
			throw new AccessDeniedException("허용되지 않은 메시지");
		}
		ChatRoom chatRoom = chatRoomMapper.findById(roomId);
		ChatMessage message = ChatMessage.builder()
				.roomNo(chatRoom.getNo())
				.userNo(sessionDetail.getUserNo())
				.createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
				.unreadCnt((long) chatRoom.getChattersCnt() - sessionInfoMapper.getUserCntInRoom(roomId))
				.text(text)
				.build();
		
		messagingTemplate.convertAndSend(String.format("/topic/chat/%s", roomId), message);
		chatMessageMapper.insertChatMessage(message);
	}
}
