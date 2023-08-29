package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.service.UserProfileService;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
@Slf4j
public class MyController {

	private final UserProfileService myService;
	
	@PostMapping("/profile")
	public String changeProfile(UserProfileForm userProfileForm) {
		
		log.info("form ->{}, {}, {}, {}, {}",userProfileForm.getNo(), userProfileForm.getId(), userProfileForm.getNickname(), userProfileForm.getFilename(), userProfileForm.getIsDefault());
		myService.changeProfile(userProfileForm);
		
		return "redirect:/login";
	}
}
