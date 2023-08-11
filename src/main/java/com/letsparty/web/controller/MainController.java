package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.letsparty.service.UserService;

import lombok.RequiredArgsConstructor;

import com.letsparty.web.form.SignupForm;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final UserService userService;

	@GetMapping("/")
	public String home() {
		return "page/main/home";
	}
	
	@GetMapping("/login")
	public String login() {
		return "page/main/login";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "page/main/signup";
	}

	@PostMapping("/signup")
	public String signup(SignupForm signupForm) {
		userService.signupUser(signupForm);
		return "redirect:/";
	}
    
	@GetMapping("/party-create")
	public String partyCreate() {
		return "page/main/party-create";
	}
}
