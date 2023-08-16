package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatController {

	@GetMapping("/{roomId}")
	public String home(@PathVariable String roomId) {
		// uuid를 사용하여 작업을 수행합니다.
		return "page/chat/home";
	}

}