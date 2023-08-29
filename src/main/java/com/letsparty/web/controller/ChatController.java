package com.letsparty.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatRoomService;
	
	// 목록에 보이는 채팅방 열기
	@GetMapping("/{roomId}")
	public String home(@PathVariable String roomId, @AuthenticationPrincipal LoginUser loginUser, HttpSession session) {
		if (!chatRoomService.joinRoomIfPossible(loginUser.getId(), loginUser.getNo(), roomId)) {
			return "redirect:/";
		}
		session.setAttribute("roomId", roomId);
		return "page/chat/home";
	}
	
	// 1:1 채팅방 접속. 없으면 개설 후 접속.
	@GetMapping("/room/{partyNo}")
	public String createRoom(@AuthenticationPrincipal LoginUser loginUser, @PathVariable int partyNo, @RequestParam("invitee_nos") List<Integer> inviteeNos) {
		String roomId = chatRoomService.createPrivateRoom(loginUser, partyNo, inviteeNos);
		return String.format("redirect:/chat/%s", roomId);
	}
	
	@GetMapping("/open-room/{partyNo}")
	public String createOpenRoom(@AuthenticationPrincipal LoginUser loginUser, @PathVariable int partyNo, @RequestParam String name, @RequestParam String description) {
		// TODO
		return "redirect:/chat/{roomId}";
	}
}