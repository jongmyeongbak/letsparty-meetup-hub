package com.letsparty.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ChatController {

	private final ChatService chatService;
	
	// 목록에 보이는 채팅방 열기
	@GetMapping("/{roomId}")
	public String home(@PathVariable String roomId, @AuthenticationPrincipal LoginUser loginUser, HttpSession session, Model model) {
		if (!chatService.joinRoomIfPossible(loginUser.getId(), loginUser.getNo(), roomId)) {
			return "redirect:/";
		}
		session.setAttribute("roomId", roomId);
		model.addAttribute("myNo", loginUser.getNo());
		return "page/chat/home";
	}
	
	// 비공개 채팅방 개설 및 접속
	//	단, 1:1 채팅인 경우 이미 방이 있으면 기존 방에 접속
	@GetMapping("/room/{partyNo}")
	public String createRoom(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, @RequestParam("invitee_nos") List<Integer> inviteeNos) {
		String roomId = chatService.createPrivateRoom(partyNo, loginUser, inviteeNos);
		return String.format("redirect:/chat/%s", roomId);
	}
	
	@GetMapping("/open-room/{partyNo}")
	public String createOpenRoom(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, @RequestParam String title, @RequestParam String description) {
		String roomId = chatService.createOpenRoom(partyNo, loginUser, title, description);
		return String.format("redirect:/chat/%s", roomId);
	}
	
	@PostMapping("/exit")
	@ResponseBody
	public ResponseEntity<String> exitRoom(@RequestParam String roomId, @AuthenticationPrincipal LoginUser loginUser) {
		try {
			chatService.exitRoom(loginUser.getId(), loginUser.getNo(), roomId);
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}
}