package com.letsparty.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.CategoryService;
import com.letsparty.service.PartyService;
import com.letsparty.util.PartyDataUtils;
import com.letsparty.vo.Party;
import com.letsparty.vo.PartyReq;
import com.letsparty.web.form.PartyForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/party")
@Slf4j
public class PartyController {
	
	private final PartyService partyService;
	private final CategoryService categoryService;
	@Value("${s3.path.covers}")
	private String coversPath;

	
	@GetMapping("/{partyNo}/member")
	public String member(@PathVariable int partyNo) {
		// partyNo를 사용하여 파티 멤버를 조회하고 작업을 수행합니다.
		return "page/party/member";
	}
	
	@GetMapping("/{partyNo}/setting/modify")
	public String modify(@PathVariable int partyNo, Model model, @AuthenticationPrincipal LoginUser user) {
		// 저장된 파티 기본 정보 조회
		Party savedParty = partyService.getPartyByNo(partyNo);
		
		// 수정을 시도한 유저가 파티의 리더가 아니라면 설정화면으로 리다이렉트
		if(!user.getId().equals(savedParty.getLeader().getId())) {
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
		List<PartyReq> savedPartyReqs = partyService.getPartyReqsByNo(partyNo);
		for (PartyReq req : savedPartyReqs) {
			switch (req.getName()) {
			case "생년1":
				partyForm.setBirthStart(req.getValue());
				break;
			case "생년2":
				partyForm.setBirthEnd(req.getValue());
				break;
			case "성별":
				partyForm.setGender(req.getValue());
				break;
			}
		}
		
		// 커버 조회
		// 클라우드 프론트 도메인과 DB에 저장된 파일이름 결합
		String savedFileName = savedParty.getFilename();
		partyForm.setSavedName(coversPath + savedFileName);

		model.addAttribute("partyForm", partyForm );
		
		return "page/party/modify";
	}
	
	// 파티 정보 수정
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
		
		return "redirect:/party/" + partyNo + "/setting" ;
	}
	
	@GetMapping("/{partyNo}")
	public String home(@PathVariable int partyNo, Model model) {
		// partyNo를 사용하여 파티 게시물을 조회합니다.
		return "page/party/home";
	}
	
	@GetMapping("/{partyNo}/setting")
	public String setting() {
		return "page/party/psetting";
	}
	
	@GetMapping("/{partyNo}/attachment")
	public String attachment(@PathVariable int partyNo){
		// partyNo를 사용해서 파티첨부파일을 조회하고 작업을 수행합니다.
		return"page/party/attachment";
	}
	
	@GetMapping("/{partyNo}/event")
	public String event(@PathVariable int partyNo) {
		// partyNo를 사용하여 파티 정보를 조회하고 작업을 수행합니다.
		return "page/party/event";
  }
}