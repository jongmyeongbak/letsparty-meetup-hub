package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.letsparty.web.form.SignupForm;

@Controller
public class MainController {

	@GetMapping("/")
	public String home() {
		return "page/main/home";
	}
  
	@GetMapping("/login")
	public String login() {
		return "page/main/login";
	}
	
	@GetMapping("/signup")
	public String form(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "page/main/signup";
	}
    
	@GetMapping("/party-create")
	public String partyCreate() {
		return "page/main/party-create";
	}
}
