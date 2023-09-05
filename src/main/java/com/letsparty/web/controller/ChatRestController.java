package com.letsparty.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letsparty.dto.ChatUserResponse;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {

	private final ChatService chatService;
	
	@GetMapping("/members/{roomId}")
	public ResponseEntity<List<ChatUserResponse>> getUsersInRoom(@PathVariable final String roomId, @AuthenticationPrincipal LoginUser loginUser) {
		List<ChatUserResponse> chatUsers = chatService.getUsersByRoomId(roomId, loginUser.getNo());
		return ResponseEntity.ok(chatUsers);
	}
	
	@GetMapping("/members/{RoomId}/{userNo}")
	public ResponseEntity<ChatUserResponse> getUserInRoom(@PathVariable final String roomId, @PathVariable final int userNo) {
		ChatUserResponse chatUser = chatService.getUserByRoomIdAndUserNo(roomId, userNo);
		return ResponseEntity.ok(chatUser);
	}
}
