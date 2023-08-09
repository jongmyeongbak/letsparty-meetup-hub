package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/letparty")
public class LetPartyController {
	
	@GetMapping
	public String home(Model model) {
		return "page/letparty/home";
	}
}
