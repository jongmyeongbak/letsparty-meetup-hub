package com.letsparty.web.controller;


import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.LetsPartyService;
import com.letsparty.service.PartyService;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor 
@SessionAttributes("signupForm")
public class MainController {
	
	private final PartyService partyService;
	private final LetsPartyService letsPartyService;

	@GetMapping("/")
	public String home(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		List<LetsPartyPost> lpPosts = letsPartyService.getPostsLimit5();
		List<Party> parties = partyService.getPartiesLimit5();
		
		model.addAttribute("lpPosts", lpPosts);
		model.addAttribute("parties", parties);
		
		// 로그인 상태이면 내가 가입한 파티의 정보를 생성 날짜 기준으로 최근 5개를 가져와 표시한다.
		if (loginUser != null) {
			List<Party> userParty = partyService.getpartyByUserId(loginUser.getId());
			model.addAttribute("userParty", userParty);			
		}
		
		return "page/main/home";
	}
	
	@GetMapping("/login")
	public String login() {
		return "page/main/login";
	}
	

	
}
