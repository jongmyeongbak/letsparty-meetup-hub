package com.letsparty.web.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.letsparty.exception.DuplicateEmailException;
import com.letsparty.exception.DuplicateUserIdException;
import com.letsparty.security.user.CustomOAuth2User;
import com.letsparty.service.AuthenticationService;
import com.letsparty.service.UserProfileService;
import com.letsparty.service.UserService;
import com.letsparty.service.ValidationService;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.SignupForm;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
@SessionAttributes("signupForm")
@Slf4j
public class SignupController {
	
	private final UserService userService;
	private final UserProfileService myService;
	private final AuthenticationService authenticationService;
	private final ValidationService validationService;
		
	//	첫번째 회원가입 화면 진입
	@GetMapping
	public String signup(Model model) {
		SignupForm signupForm = new SignupForm();
		model.addAttribute("signupForm", signupForm);
	
		return "page/signup/step1";
	}
	
	//	첫번째 회원가입에서 값 입력하고 POST 전송
	@PostMapping("/step1")
	public String signup1(@ModelAttribute("signupForm") SignupForm signupForm, BindingResult errors) {
		
		boolean checkDuplicate = false;
		
		log.info("signupForm 정보 -> {}", signupForm.toString());
		
		checkDuplicate = validationService.checkstep1(signupForm, errors);
		
		if (checkDuplicate) {
			return "page/signup/step1";
		}
		
		return "page/signup/step2";
	}
	
	//	두번째 회원가입에서 값 입력하고 POST 전송
	@PostMapping("/step2")
	public String signup2(@ModelAttribute("signupForm") SignupForm signupForm, BindingResult errors, SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
		
		boolean checkDuplicate = false;
		
		log.info("signupForm 정보 -> {}", signupForm.toString());
		
		checkDuplicate = validationService.checkstep2(signupForm, errors);
	
		if (checkDuplicate) {
			return "page/signup/step2";
		}
	
		try {
			userService.signupUser(signupForm);
		} catch (DuplicateUserIdException ex) {
			errors.rejectValue("id", null, "사용할 수 없는 아이디입니다.");
			return "page/signup/step1";
		} catch (DuplicateEmailException ex) {
			errors.rejectValue("email", null, "사용할 수 없는 이메일입니다.");
			return "page/signup/step1";
		}
		
		UserProfileForm userProfileForm = UserProfileForm.builder()
				.id(signupForm.getId())
				.nickname(signupForm.getName())
				.filename("/images/party/profile-default.png")
				.isDefault(true)
				.isUrl(true)
				.build();
		
		int profileNo = myService.addProfile(userProfileForm);
		
		redirectAttributes.addFlashAttribute("user", userProfileForm);
		redirectAttributes.addFlashAttribute("profileNo", profileNo);
		
		sessionStatus.setComplete();
		
		return "redirect:/signup/complete";
	}
	
	@GetMapping("/oauth-signup")
	public String ouathSignup(Model model) {
		SignupForm signupForm = new SignupForm();
		model.addAttribute("signupForm", signupForm);
	
		return "page/signup/oauth-signup";
	}
	
	@PostMapping("/oauth-signup")
	public String oauthSignup(@AuthenticationPrincipal CustomOAuth2User user, SignupForm signupForm, BindingResult errors, RedirectAttributes redirectAttributes) {
//		oauth 관련 추가 회원가입 정보 기입
		signupForm.setId(user.getId());
		signupForm.setEmail(user.getEmail());
		signupForm.setName(user.getRealname());
		System.out.println("되돌앋오기");

		boolean checkDuplicate = false;
		
		checkDuplicate = validationService.checkstep2(signupForm, errors);
		System.out.println(errors);
		
		if (checkDuplicate) {
			return "page/signup/oauth-signup";
		}
		
		userService.updateUser(signupForm);
		
		UserProfile userProfile = myService.getDefaultProfile(user.getId());
		
		redirectAttributes.addFlashAttribute("user", userProfile);
		redirectAttributes.addFlashAttribute("profileNo", userProfile.getNo());
		
		userService.updateRoleById(user.getId(), 4);
		
		authenticationService.changeAuthentication(user.getId());
		
		return "redirect:/signup/complete";
	}
	
	@GetMapping("/complete")
	public String signupComplete() {
		
		return "page/signup/complete";
	}

}
