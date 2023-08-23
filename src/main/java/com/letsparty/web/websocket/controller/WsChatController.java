package com.letsparty.web.websocket.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.letsparty.web.websocket.dto.ChatMessageDto;
import com.letsparty.web.websocket.service.WsChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WsChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final WsChatService wsChatService;

//	@MessageMapping("/chat/{roomId}/join")
//	public void enter(@DestinationVariable final String roomId, ChatMessageDto message) {
//		message.setText(message.getUserNo() + "님이 접속하셨습니다.");
//		messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
//	}

	@MessageMapping("/chat/{roomId}")
//	@SendTo("/topic/chat/{roomId}")
	public ChatMessageDto sendMessage(@DestinationVariable final String roomId, @Payload ChatMessageDto message,
			Principal principal, @Headers Map<String, Object> headers) {
		wsChatService.handleSendMessage(message, roomId, headers);
		
		log.info("SEND headers: {}", headers);
		log.info("principal: {}", principal);
		log.info("message.toString: {}", message);
		return message;
	}
//	
//	@MessageMapping("/chat/{roomId}")
//	@SendTo("/topic/chat/{roomId}")
//	public ChatMessageDto sendMessage(@DestinationVariable final String roomId, @Payload ChatMessageDto message,
//			Principal principal, @Headers Map<String, Object> headers) {
//		log.info("chat");
//		log.info("principal: {}", principal);
//		log.info("{headerRoomId: {}", (String) headers.get("roomId"));
//		log.info("{roomId: {}, chatMessageDto: {}}", roomId, message);
////		chatMessageDto.setSenderNo((Authentication) principal);
////		(UserDetails) ((Authentication) principal);
//		return message;
//	}

//	@MessageMapping("/chat/{roomId}")
//	@SendTo("/topic/chat/{roomId}")
//	public ChatMessageDto send(final ChatRequ)
//	public OutputMessage send(Message)
//	new SimpleDateFormat("HH:mm").format(new Date());
}
