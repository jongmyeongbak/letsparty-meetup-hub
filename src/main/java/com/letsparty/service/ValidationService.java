package com.letsparty.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import com.letsparty.exception.DuplicateEmailException;
import com.letsparty.exception.DuplicateUserIdException;
import com.letsparty.vo.Party;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.web.form.SignupForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationService {
	
	private final UserService userService;
	private final PartyService partyService;
	private final UserPartyApplicationService userPartyApplicationService;

	public boolean checkstep1(SignupForm signupForm, BindingResult errors) {

		boolean checkDuplicate = false;

		// id 유효값 검사
		if (!StringUtils.hasText(signupForm.getId())) {
			errors.rejectValue("id", null, "아이디는 필수입력값입니다.");
			checkDuplicate = true;
		} else {
			try {
				userService.checkDuplicateUserId(signupForm.getId());
			} catch (DuplicateUserIdException ex) {
				errors.rejectValue("id", null, "사용할 수 없는 아이디입니다.");
				checkDuplicate = true;
			}
		}
		
		// 비밀번호 유효값 검사
		if (!StringUtils.hasText(signupForm.getPassword())) {
			errors.rejectValue("password", null, "비밀번호는 필수입력값입니다.");
			checkDuplicate = true;
		}
		
		// 이름 유효값 검사
		if (!StringUtils.hasText(signupForm.getName())) {
			errors.rejectValue("name", null, "이름은 필수입력값입니다.");
			checkDuplicate = true;
		}
		
		// 이메일 유효값 검사
		if (!StringUtils.hasText(signupForm.getEmail())) {
			errors.rejectValue("email", null, "이메일은 필수입력값입니다.");
			checkDuplicate = true;
		} else {
			try {
				userService.checkDuplicateEmail(signupForm.getEmail());
			} catch (DuplicateEmailException ex) {
				errors.rejectValue("email", null, "사용할 수 없는 이메일입니다.");
				checkDuplicate = true;
			}
		}
		
		return checkDuplicate;
	}

	public boolean checkstep2(SignupForm signupForm, BindingResult errors) {
		
		boolean checkDuplicate = false;

		// 전화번호 유효값 검사
		if (!StringUtils.hasText(signupForm.getTel())) {
			errors.rejectValue("tel", null, "전화번호는 필수입력값입니다.");
			checkDuplicate = true;
		}
		
		// 생년월일 유효값 검사
		if (signupForm.getBirthday() == null) {
			errors.rejectValue("birthday", null, "생년월일은 필수입력값입니다.");
			checkDuplicate = true;
		}
		
		// 성별 유효값 검사
		if (!StringUtils.hasText(signupForm.getGender())) {
			errors.rejectValue("gender", null, "성별은 필수입력값입니다.");
			checkDuplicate = true;
		}
		
		return checkDuplicate;
	}
	
	// 멤버 탈퇴시키기 검사
    public Map<String, Object> kickOutUser(int partyNo, String userId, String loginUser) {
        Map<String, Object> response = new HashMap<>();
        UserPartyApplication savedUserPartyApplication = userPartyApplicationService.findByPartyNoAndUserId(partyNo, userId);
        Party savedParty = partyService.getPartyByNo(partyNo);

        if (savedUserPartyApplication == null) {
            response.put("status", "error");
            response.put("message", "유저를 찾을 수 없습니다.");
            return response;
        }
        if (!savedParty.getLeader().getId().equals(loginUser)) {
            response.put("status", "error");
            response.put("message", "리더만 멤버를 탈퇴 시킬 수 있습니다.");
            return response;
        }
        if (savedParty.getLeader().getId().equals(userId)) {
            response.put("status", "error");
            response.put("message", "리더는 자기 자신을 탈퇴 시킬 수 없습니다.");
            return response;
        }
        userPartyApplicationService.kick(savedUserPartyApplication, savedParty);
        response.put("status", "success");
        response.put("message", "퇴장 처리가 완료되었습니다.");
        return response;
	}


}
