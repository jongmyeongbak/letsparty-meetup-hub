package com.letsparty.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor 
@SessionAttributes("signupForm")
public class MainController {

	@GetMapping("/")
	public String home() {
		return "page/main/home";
	}
	
	@GetMapping("/login")
	public String login() {
		return "page/main/login";
	}
	

	
}
