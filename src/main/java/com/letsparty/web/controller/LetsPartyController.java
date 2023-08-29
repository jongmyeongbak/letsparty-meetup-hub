package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/letsparty")
public class LetsPartyController {
	
	@GetMapping
	public String home(Model model) {
		return "page/letsparty/home";
	}
	
	// 렛츠파티 게시글 작성 화면으로 이동
	@GetMapping("/post")
	public String post() {
		return "page/letsparty/post";
	}
	
	// 렛츠파티 게시글 상세 화면으로 이동
	@GetMapping("/post/{postNo}")
	public String detail(@PathVariable int postNo) {
		return "page/letsparty/detail";
	}
}
