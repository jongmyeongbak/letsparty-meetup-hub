package com.letsparty.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	
	// 참가 중인 채팅방 접속
	@GetMapping("/{roomId}")
	public String home(@PathVariable String roomId, HttpSession session, Model model) {
		// TODO: 방 참여자인지 validate
//		model.addAttribute("roomId", roomId);
		session.setAttribute("roomId", roomId);
		return "page/chat/home";
	}
	
	// 개설되어 있는 채팅방 입장
	@PostMapping("/{roomId}")
	public String home(@PathVariable String roomId) {
		// TODO
		return "redirect:/chat/{roomId}";
	}

}