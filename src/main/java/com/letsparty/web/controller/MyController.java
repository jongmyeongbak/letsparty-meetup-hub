package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.service.MyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {

	private final MyService myService;
	
	@PostMapping("/profile")
	public String changeProfile() {
		
		return "redirect:/";
	}
}
