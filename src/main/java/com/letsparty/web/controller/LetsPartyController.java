package com.letsparty.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.letsparty.exception.PostNotFoundException;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.LetsPartyService;
import com.letsparty.service.UserPartyApplicationService;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.web.form.LetsPartyPostForm;
import com.letsparty.web.model.LetsPartyPostList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/letsparty")
@Slf4j
public class LetsPartyController {
	
	private final UserPartyApplicationService userPartyApplicationService;
	private final LetsPartyService letsPartyService;
	@Value("${s3.path.covers}")
	private String coversPath;
	
	@GetMapping
	public String home(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		// 렛츠파티 home에 접근시 리스트를 표시할 최초 조건 
		Map<String, Object> param = new HashMap<>();
		param.put("sort", "latest");
		param.put("rows", 10);
		param.put("page", 1);
		
		LetsPartyPostList result = letsPartyService.getPosts(param);
		
		model.addAttribute("result", result);
		model.addAttribute("isLeader", userPartyApplicationService.isLeader(loginUser));
		return "page/letsparty/home";
	}
	
	// 렛츠파티 게시글 작성 화면으로 이동
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/post")
	public String post(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		if (!userPartyApplicationService.isLeader(loginUser)) {
	        return "redirect:/letsparty";
	    }
	    LetsPartyPostForm letsPartyPostForm = new LetsPartyPostForm();
	    addUserPartyApplicationsToModel(loginUser, model);
	    model.addAttribute("letsPartyPostForm", letsPartyPostForm);
	    return "page/letsparty/post";
	}
	
	// 렛츠파티 게시글 생성
	@PostMapping("/post")
	public String post (@Valid LetsPartyPostForm letsPartyPostForm, BindingResult error, 
			@AuthenticationPrincipal LoginUser loginUser, Model model) {
		if (error.hasErrors()) {
	        addUserPartyApplicationsToModel(loginUser, model);
	        return "page/letsparty/post";
	    }
	    letsPartyService.insertPost(letsPartyPostForm, loginUser.getId());
	    return "redirect:/letsparty";
	}
	
	// 렛츠파티 리스트 표시
	@GetMapping("/search")
	public String search(@RequestParam(name = "categoryNo", required = false, defaultValue = "0") Integer categoryNo,
						 @RequestParam(name = "sort", required = false, defaultValue = "latest") String sort,
						 @RequestParam(name = "rows", required = false, defaultValue = "10") int rows,
						 @RequestParam(name = "page", required = false, defaultValue = "1") int page,
						 @RequestParam(name = "opt", required = false) String opt,
						 @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
						 @AuthenticationPrincipal LoginUser loginUser, Model model) {
		
		log.info("categoryNo={}, sort='{}', rows='{}', page='{}', opt='{}', keyword='{}'", categoryNo, sort, rows, page, opt, keyword);
		
		Map<String, Object> param = new HashMap<>();
		param.put("categoryNo", categoryNo);
		param.put("sort", sort);
		param.put("rows", rows);
		param.put("page", page);
		if (StringUtils.hasText(opt) && StringUtils.hasText(keyword)) {
			param.put("opt", opt);
			param.put("keyword", keyword);
		}
		
		LetsPartyPostList result = letsPartyService.getPosts(param);
		
		model.addAttribute("result", result);
		model.addAttribute("isLeader", userPartyApplicationService.isLeader(loginUser));
		return "page/letsparty/home";
	}
	
	// 렛츠파티 게시글 상세 화면으로 이동
	@GetMapping("/post/{postNo}")
	public String detail(@PathVariable long postNo, Model model, RedirectAttributes attributes) {
	    try {
	        LetsPartyPost post = letsPartyService.getPostDetail(postNo);
	        post.getParty().setFilename(coversPath + post.getParty().getFilename());
	        model.addAttribute("post", post);
	        return "page/letsparty/detail";
	    } catch (PostNotFoundException e) {
	    	attributes.addFlashAttribute("errorMessage", e.getMessage());
	        return "redirect:/letsparty";
	    }
	}
	
	// 게시물의 조회수를 늘림
	@GetMapping("/read/{postNo}")
	public String read(@PathVariable Long postNo, RedirectAttributes attributes) {
	    try {
	    	letsPartyService.increaseReadCount(postNo);
	    	return "redirect:/letsparty/post/{postNo}";
	    } catch (PostNotFoundException e) {
	    	attributes.addFlashAttribute("errorMessage", e.getMessage());
	    	return"redirect:/letsparty";
		}
	}

	private void addUserPartyApplicationsToModel(LoginUser loginUser, Model model) {
	    List<UserPartyApplication> userPartyApplications = userPartyApplicationService.findAllByUserId(loginUser.getId());
	    model.addAttribute("userPartyApplications", userPartyApplications);
	}
	
}
