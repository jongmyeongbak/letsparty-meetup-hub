package com.letsparty.web.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.web.form.RegisterUserForm;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping("/login")
	public String login() {
		return "user/loginform";
	}
	
	@GetMapping("/register")
	public String form(Model model) {
		model.addAttribute("registerMemberForm", new RegisterUserForm());
		return "user/form";
	}
}
