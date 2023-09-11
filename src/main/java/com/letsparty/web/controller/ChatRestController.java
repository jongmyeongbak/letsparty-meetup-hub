package com.letsparty.web.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letsparty.dto.ChatUserResponse;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ChatRestController {

	private final ChatService chatService;

	@GetMapping("/init/{roomId}")
	public ResponseEntity<Object[]> getUsersInRoom(@PathVariable final String roomId,
			@AuthenticationPrincipal LoginUser loginUser) {
		Object[] initInfo = chatService.getInitInfoByRoomId(roomId, loginUser.getNo());
		if (initInfo == null) {
			return ResponseEntity.badRequest().body(null);
		}
		return ResponseEntity.ok(initInfo);
	}

	@GetMapping("/members/{roomId}/{userNo}")
	public ResponseEntity<ChatUserResponse> findUserInPartyByRoomIdAndUserNo(@PathVariable final String roomId,
			@PathVariable final int userNo) {
		ChatUserResponse chatUser = chatService.getUserByRoomIdAndUserNo(roomId, userNo);
		return ResponseEntity.ok(chatUser);
	}

	@PostMapping("/members/{roomId}")
	public List<ChatUserResponse> findUserInPartyByRoomIdAndUserNo(@PathVariable final String roomId,
			@RequestBody List<Integer> userNos) {
		Set<Integer> userNosSet = new HashSet<>(userNos);
		return chatService.getUsersByRoomIdRegardlessOfLeft(roomId, userNosSet);
	}
}
