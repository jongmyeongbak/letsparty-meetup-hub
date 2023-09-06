package com.letsparty.web.websocket.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.letsparty.web.websocket.service.WsChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WsChatController {

	private final WsChatService wsChatService;

	@MessageMapping("/chat/{roomId}")
//	@SendTo("/topic/chat/{roomId}")
	public void sendMessage(@DestinationVariable final String roomId, @Payload final String text,
			@Headers Map<String, Object> headers) {
		wsChatService.handleSendMessage(text, roomId, headers);
		
		log.info("SEND headers: {}", headers);
		log.info("text: {}", text);
	}
}
