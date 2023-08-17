package com.letsparty.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.CategoryService;
import com.letsparty.service.PartyService;
import com.letsparty.util.PartyDataUtils;
import com.letsparty.web.form.PartyCreateForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PartyCreateController {
	
	private final PartyService partyService;
	private final CategoryService categoryService;

	// 파티생성폼으로 이동
	@GetMapping("/party-create")
	public String partyCreate(@RequestParam(value = "catNo", required = false) Integer catNo, Model model) {
		// catNo파라미터가 없거나 0보다 작을 경우 루트 페이지로 리다이렉트
		if (catNo == null || catNo < 0) {
			return "redirect:/";
		}
		PartyDataUtils.addBirthYearAndCategoryList(model, categoryService);

		// 클릭한 카테고리가 셀렉트 되어있도록 함
		PartyCreateForm partyCreateForm = new PartyCreateForm();
		partyCreateForm.setCategoryNo(catNo);

		model.addAttribute("partyCreateForm", partyCreateForm);

		return "page/main/party-create";
	}

	// 파티생성을 수행
	@PostMapping("/party-create")
	public String partyCreate(@AuthenticationPrincipal LoginUser user, @Valid PartyCreateForm partyCreateForm,
			BindingResult error, Model model) {
		
		// 최소나이(birthStart)와 최대나이(birthEnd) 검증
		int birthStart = Integer.parseInt(partyCreateForm.getBirthStart());
		int birthEnd = Integer.parseInt(partyCreateForm.getBirthEnd());
		if (birthStart < birthEnd) {
			error.rejectValue("birthStart", null, "최소나이는 최대나이보다 적어야 합니다.");
		}
		
		// 태그의 글자수 검증
		List<String> tags = partyCreateForm.getTags();
		if (tags != null && !tags.isEmpty()) {
			for (String tag : tags) {
				if (tag.length() > 20) {
					error.rejectValue("description", null, "'#" + tag + "...'태그는 20자를 초과하였습니다.");
					break;
				}
			}
		}
		
		// 최소나이가 최대나이보다 많거나, 제목이 없거나, 정원 수가 10미만일 때
		if (error.hasErrors()) {
			PartyDataUtils.addBirthYearAndCategoryList(model, categoryService);
			model.addAttribute("partyCreateForm", partyCreateForm);
			return "page/main/party-create";
		}
		
		String leaderId = user.getId();
		int partyNo = partyService.createParty(partyCreateForm, leaderId);
		return "redirect:/party/" + partyNo;
	}
}
