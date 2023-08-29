package com.letsparty.web.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.LetsPartyService;
import com.letsparty.service.UserPartyApplicationService;
import com.letsparty.vo.Category;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.web.form.LetsPartyPostForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/letsparty")
@Slf4j
public class LetsPartyController {
	
	private final UserPartyApplicationService userPartyApplicationService;
	private final LetsPartyService letsPartyService;
	
	@GetMapping
	public String home(Model model) {
		return "page/letsparty/home";
	}
	
	// 렛츠파티 게시글 작성 화면으로 이동
	@GetMapping("/post")
	public String post(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		List<UserPartyApplication> UserPartyApplications = userPartyApplicationService.findAllByUserId(loginUser.getId());
		LetsPartyPostForm letsPartyPostForm = new LetsPartyPostForm();
		
		model.addAttribute("letsPartyPostForm", letsPartyPostForm);
		model.addAttribute("UserPartyApplications", UserPartyApplications);
		return "page/letsparty/post";
	}
	
	// 렛츠파티 게시글 생성
	@PostMapping("/post")
	public String post (LetsPartyPostForm letsPartyPostForm, @AuthenticationPrincipal LoginUser user) {
		log.info("포스트 폼 값 ====> {}", letsPartyPostForm);
		letsPartyService.insertPost(letsPartyPostForm, user.getId());
		
		return "";
	}
	
	// 렛츠파티 게시글 상세 화면으로 이동
	@GetMapping("/post/{postNo}")
	public String detail(@PathVariable int postNo, Model model) {
		return "page/letsparty/detail";
	}
	
}
