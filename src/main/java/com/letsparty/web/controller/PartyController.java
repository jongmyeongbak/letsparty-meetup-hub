package com.letsparty.web.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.letsparty.dto.BeginEndPostNo;
import com.letsparty.dto.PartyReqDto;
import com.letsparty.dto.PostAttachment;
import com.letsparty.dto.SimplePostDto;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.CategoryService;
import com.letsparty.service.MediaService;
import com.letsparty.service.PartyService;
import com.letsparty.service.PostService;
import com.letsparty.service.UserPartyApplicationService;
import com.letsparty.service.UserProfileService;
import com.letsparty.util.PartyDataUtils;
import com.letsparty.vo.Party;
import com.letsparty.vo.Post;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.PartyForm;
import com.letsparty.web.form.PostForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/party")
@Slf4j
public class PartyController {
	
	private final PartyService partyService;
	private final PostService postService;
	private final MediaService mediaService;
	private final CategoryService categoryService;
	private final UserProfileService userProfileService;
	private final UserPartyApplicationService userPartyApplicationService;
	@Value("${s3.path.covers}")
	private String coversPath;
	@Value("${s3.path.profiles}")
	private String profilesPath;
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/join")
	public String joinParty(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, Model model) {
		List<UserProfile> userProfiles = userProfileService.getAllProfileByUserId(loginUser.getId());
		PartyReqDto partyReqs = partyService.getPartyReqsByNo(partyNo);
		
		model.addAttribute("userProfiles", userProfiles);
		model.addAttribute("partyReqs", partyReqs);
		
		return "/page/party/join-party";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{partyNo}/join")
	public String joinParty(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, @RequestParam("profileNo") int profileNo) {
		// 파티 가입 조건과 일치하면 가입을 승인한다.
		UserProfile profile = new UserProfile();
		profile.setNo(profileNo);
		if (!userPartyApplicationService.addUserPartyApplicationIfReqMet(partyNo, loginUser.getId(), profile)) {
			return "redirect:/party/{partyNo}/join?req=fail";
		};
		return "redirect:/party/{partyNo}";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/member")
	public String member(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, Model model) {
		if (!partyService.isPartyMember(loginUser.getId(), partyNo)) {
			return "redirect:/party/{partyNo}";
		}
		List<UserPartyApplication> userPartyApplications = partyService.getUserPartyApplications(partyNo, loginUser.getNo());
		model.addAttribute("users", userPartyApplications);
		model.addAttribute("partyNo", partyNo);
		model.addAttribute("profilesPath", profilesPath);
		return "page/party/member";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/setting/modify")
	public String modify(@PathVariable int partyNo, Model model, @AuthenticationPrincipal LoginUser user) {
		// 저장된 파티 기본 정보 조회
		Party savedParty = partyService.getPartyByNo(partyNo);
		
		// 수정을 시도한 유저가 파티의 리더가 아니라면 설정화면으로 리다이렉트
		if(!savedParty.getLeader().getId().equals(user.getId())) {
			return "redirect:/party/{partyNo}/setting";
		}
		
		PartyDataUtils.addBirthYearAndCategoryList(model, categoryService);
		
		// 수정폼 생성
		PartyForm partyForm = new PartyForm();
		
		partyForm.setCategoryNo(savedParty.getCategory().getNo());
		partyForm.setName(savedParty.getName());
		partyForm.setQuota(savedParty.getQuota());
		partyForm.setGender(savedParty.getName());
		if (savedParty.getDescription() != null) {
			partyForm.setDescription(savedParty.getDescription());
		}
		
		// 저장된 파티 조건 조회
		PartyReqDto partyReqs = partyService.getPartyReqsByNo(partyNo);
		partyForm.setBirthStart(partyReqs.getBirthStart());
		partyForm.setBirthEnd(partyReqs.getBirthEnd());
		partyForm.setGender(partyReqs.getGender());
		
		switch (partyReqs.getGender()) {
		case "모두":
			partyForm.setGender("A");
			break;
		case "남성":
			partyForm.setGender("M");
			break;
		case "여성":
			partyForm.setGender("F");
			break;
		}
		
		// 커버 조회
		// 클라우드 프론트 도메인과 DB에 저장된 파일이름 결합
		String savedFileName = savedParty.getFilename();
		partyForm.setSavedName(coversPath + savedFileName);

		model.addAttribute("partyForm", partyForm );
		
		return "page/party/modify";
	}
	
	// 파티 정보 수정
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{partyNo}/setting/modify") 
	public String modify(@PathVariable int partyNo, @Valid PartyForm partyForm, BindingResult error, Model model) {
		// 최소나이(birthStart)와 최대나이(birthEnd) 검증
		int birthStart = Integer.parseInt(partyForm.getBirthStart());
		int birthEnd = Integer.parseInt(partyForm.getBirthEnd());
		if (birthStart < birthEnd) {
			error.rejectValue("birthStart", null, "최소나이는 최대나이보다 적어야 합니다.");
		}
		
		// 각 태그의 글자수 검증
		List<String> tags = partyForm.getTags();
		if (tags != null && !tags.isEmpty()) {
			for (String tag : tags) {
				if (tag.length() > 20) {
					error.rejectValue("description", null, "'#" + tag + "...'태그는 20자를 초과하였습니다.");
					break;
				}
			}
		}
		
		// 유효성 검사 실패시에 수정 폼으로 돌아간다.
		if (error.hasErrors()) {
			Party savedParty = partyService.getPartyByNo(partyNo);
			String savedFileName = savedParty.getFilename();
			partyForm.setSavedName(coversPath + savedFileName);
			
			PartyDataUtils.addBirthYearAndCategoryList(model, categoryService);
			model.addAttribute("partyForm", partyForm);
			return "page/party/modify";
		}
		
		partyService.modifyParty(partyForm, partyNo);
		
		return "redirect:/party/{partyNo}/setting" ;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{partyNo}/delete")
	public String deleteParty(@AuthenticationPrincipal LoginUser loginUser, @PathVariable int partyNo, RedirectAttributes attributes) {
		Party savedParty = partyService.getPartyByNo(partyNo);
	    if (!savedParty.getLeader().getId().equals(loginUser.getId())) {
	        return "redirect:/party/{partyNo}";
	    }
	    UserPartyApplication savedUpa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, loginUser.getId());
	    if (!userPartyApplicationService.isPartyDeletable (partyNo)) {
	    	attributes.addFlashAttribute("errorMessage", "파티에 멤버가 남아있으므로 파티를 삭제할 수 없습니다.");
	    	return "redirect:/party/{partyNo}/setting";
	    } else {
	    	partyService.deleteParty(savedParty, savedUpa);
	    	return "redirect:/";
	    }
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/post-form")
	public String addPost(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, Model model) {
		UserPartyApplication upa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, loginUser.getId());
		model.addAttribute("upa", upa);
		PostForm postForm = new PostForm();
		model.addAttribute("postForm", postForm);
		return "/page/party/post-form";
	}
	
	// 게시물 제출
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{partyNo}/post-form")
	public String addPost(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, @Valid PostForm postForm,
			BindingResult error, Model model) {
		UserPartyApplication upa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, loginUser.getId());
		model.addAttribute("upa", upa);
		// TODO 제목, 본문 글자 수 제한하는 유효성 구현하기
		if (error.hasErrors()) {
			return "page/party/post-form";
		}
		partyService.insertPost(postForm, partyNo, loginUser.getId());
		return "redirect:/party/{partyNo}";
	}
	
	@GetMapping("/{partyNo}")
	public String home(@PathVariable int partyNo) {
		// partyNo를 사용해서 마지막 게시글 번호를 불러온다.
		int postNo = postService.getLastPostNoByPartyNo(partyNo);
		return "redirect:/party/{partyNo}/read/" + postNo;
	}
	
	@GetMapping("/{partyNo}/read/{postNo}")
	public String read(@PathVariable("partyNo") int partyNo, @PathVariable("postNo") int postNo) {
		if (postNo != 0) {
			postService.readIncrement(partyNo, postNo);
		}
		return "redirect:/party/{partyNo}/post/{postNo}";
	}
	
	@GetMapping("/{partyNo}/post/{postNo}")
	public String readPost(@PathVariable("partyNo") int partyNo, @PathVariable("postNo") int postNo, Principal principal, Authentication authentication, Model model) {
		if (principal != null && authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_USER_PENDING"))) {
			UserPartyApplication loginUpa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, principal.getName());
			if (null != loginUpa) {
				model.addAttribute("loginUpa", loginUpa);
			}
			model.addAttribute("loginUser", principal.getName());
		}
		
		if (postNo != 0) {
			// 해당 게시글 번호로 게시글 정보, 첨부파일 정보들을 불러와 저장한 뒤 화면에 표시한다.
			Post post = postService.getPostByPostNoAndPartyNo(partyNo, postNo);
			UserPartyApplication upa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, post.getUser().getId());
			model.addAttribute("post", post);
			model.addAttribute("upa", upa);
			
			// 게시글의 각종 첨부 파일 내용들(이미지, 동영상, 지도, 투표)
			PostAttachment pa = mediaService.getMediaByPostId(post.getId());
			model.addAttribute("pa",pa);
			
			// 해당 게시글 번호의 앞뒤로 2개씩 게시글의 제목, 닉네임, 본문, 댓글수, 조회수를 가져와 화면에 표시한다.
			List<SimplePostDto> sPostDto = postService.getSimplePostLimit5(partyNo, postNo);
			model.addAttribute("simPosts", sPostDto);
			
			// 마지막 게시글번호가 포함되면 오른쪽 정렬을 하기 위한 로직
			BeginEndPostNo beginEndNo = postService.getBeginAndEndPostNo(partyNo);
			boolean hasBeginNo = false;
			boolean hasEndNo = false;
			if (beginEndNo.getBeginNo() != 0 && beginEndNo.getBeginNo() == sPostDto.get(0).getPostNo()) {
				hasBeginNo = true;
			}
			if (beginEndNo.getEndNo() != 0 && beginEndNo.getEndNo() == sPostDto.get(sPostDto.size() - 1).getPostNo()) {
				hasEndNo = true;
			}
			
			model.addAttribute("hasBeginNo", hasBeginNo);
			model.addAttribute("hasEndNo", hasEndNo);
			
			// 현재 게시글 번호에서 3번째 이후, 이전 게시글 번호 가져오는 로직
			BeginEndPostNo thirdBeginEndNo = postService.getThirdBeginAndEndPostNo(partyNo, postNo);
			model.addAttribute("thirdBeginEndNo", thirdBeginEndNo);
		}
		return "page/party/post";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{partyNo}/poll/{postNo}")
	public String pollAnswer(@AuthenticationPrincipal LoginUser loginUser, @PathVariable int partyNo, @PathVariable int postNo, @RequestParam int optionPk) {
		log.info("optionPk -> {}", optionPk);
		partyService.answerPollOption(loginUser.getId(), optionPk);
		return "redirect:/party/{partyNo}/post/{postNo}";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/setting")
	public String setting(@PathVariable int partyNo, @AuthenticationPrincipal LoginUser loginUser, Model model) {
		UserPartyApplication upa = userPartyApplicationService.findByPartyNoAndUserId(partyNo, loginUser.getId());
		model.addAttribute("upa", upa);
		return "page/party/setting";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/withdraw/{upaNo}")
	public String withdraw(@PathVariable("partyNo") int partyNo, @PathVariable("upaNo") int upaNo) {
		if (!userPartyApplicationService.withdraw(upaNo)) {
			return "redirect:/party/{partyNo}/setting?req=fail";
		};
		return "redirect:/party/{partyNo}";
	}
	
	// 퇴장시키기 화면
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/setting/member")
	public String kickOutPage(@PathVariable int partyNo, Model model, @AuthenticationPrincipal LoginUser loginUser) {
		List<UserPartyApplication> userPartyApplications = partyService.getUserPartyApplications(partyNo, loginUser.getNo());
		Party savedParty = partyService.getPartyByNo(partyNo);
		// 자신이 해당 파티의 리더가 아니라면 리다이렉트
		if(!savedParty.getLeader().getId().equals(loginUser.getId())) {
			return "redirect:/party/{partyNo}";
		}
		model.addAttribute("users", userPartyApplications);
		model.addAttribute("partyNo", partyNo);
		model.addAttribute("profilesPath", profilesPath);
		return "page/party/kick-out";
	}
	
	// 퇴장시키기
	@PostMapping("/{partyNo}/setting/kick")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> kickOut(@PathVariable int partyNo, @RequestParam String userId, @AuthenticationPrincipal LoginUser loginUser) {
		Map<String, Object> response = userPartyApplicationService.kickOutUser(partyNo, userId, loginUser.getId());
	    if ("error".equals(response.get("status"))) {
	        return ResponseEntity.badRequest().body(response);
	    }
	    return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/attachment")
	public String attachment(@PathVariable int partyNo){
		// partyNo를 사용해서 파티첨부파일을 조회하고 작업을 수행합니다.
		return"page/party/attachment";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/album")
	public String album(@PathVariable int partyNo){
		// partyNo를 사용해서 파티첨부파일을 조회하고 작업을 수행합니다.
		return"page/party/album";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/file")
	public String file(@PathVariable int partyNo){
		// partyNo를 사용해서 파티첨부파일을 조회하고 작업을 수행합니다.
		return"page/party/file";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/poll")
	public String poll(@PathVariable int partyNo){
		// partyNo를 사용해서 파티첨부파일을 조회하고 작업을 수행합니다.
		return"page/party/poll";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{partyNo}/event")
	public String event(@PathVariable int partyNo) {
		// partyNo를 사용하여 파티 정보를 조회하고 작업을 수행합니다.
		return "page/party/event";
  }

}

