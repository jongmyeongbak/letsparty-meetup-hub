package com.letsparty.web.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.UserProfileService;
import com.letsparty.service.UserService;
import com.letsparty.util.PhoneNumberFormatter;
import com.letsparty.vo.User;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class MyController {

	private final UserProfileService myService;
	private final UserService userService;
	
	@GetMapping
	public String myPage(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		List<UserProfile> profileList = myService.getAllProfileByUserId(loginUser.getId());
		User user =  userService.getUserByName(loginUser.getId());
		user.setTel(PhoneNumberFormatter.formatPhoneNumber(user.getTel()));
		
		model.addAttribute("profiles", profileList);
		model.addAttribute("userInfo", user);
		return "/page/main/mypage";
	}
	@GetMapping("/profile")
	public String myProfile(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		List<UserProfile> profileList = myService.getAllProfileByUserId(loginUser.getId());
		model.addAttribute("profiles", profileList);
		return "/page/main/my-profile";
	}
	
	@PostMapping("/change-my-profile")
	public String changeMyProfile(@AuthenticationPrincipal LoginUser loginUser, UserProfileForm userProfileForm) {
		userProfileForm.setId(loginUser.getId());
		myService.changeProfile(userProfileForm);
		
		return "redirect:/my/profile";
	}
	
	@PostMapping("/add-my-profile")
	public String addMyprofile(@AuthenticationPrincipal LoginUser loginUser, UserProfileForm userProfileForm) {
		userProfileForm.setId(loginUser.getId());
		myService.addProfileWithLogin(userProfileForm);
		
		return "redirect:/my/profile";
	}
	
	@PreAuthorize("isAnonymous()")
	@PostMapping("/profile")
	public String changeProfile(UserProfileForm userProfileForm) {
		System.out.println("폼제출");
		log.info("form ->{}, {}, {}, {}, {}",userProfileForm.getNo(), userProfileForm.getId(), userProfileForm.getNickname(), userProfileForm.getFilename(), userProfileForm.getIsDefault());
		myService.changeProfile(userProfileForm);
		
		return "redirect:/login";
	}
	
	@GetMapping("/profile/{profileNo}")
	@ResponseBody
	public UserProfile getProfile(@PathVariable("profileNo") int profileNo) {
		return myService.getProfileByNo(profileNo);
	}
	
	@PostMapping("/delete/{profileNo}")
	@ResponseBody
	public String deleteProfile(@AuthenticationPrincipal LoginUser loginUser, @PathVariable("profileNo") int profileNo) {
		System.out.println(profileNo);
		myService.deleteProfile(profileNo, loginUser.getId());
		return "redirect:my/profile";
	}
}
