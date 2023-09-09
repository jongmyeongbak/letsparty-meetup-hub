package com.letsparty.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.PartyMapper;
import com.letsparty.mapper.PartyReqMapper;
import com.letsparty.mapper.UserMapper;
import com.letsparty.mapper.UserPartyApplicationMapper;
import com.letsparty.mapper.UserProfileMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.Party;
import com.letsparty.vo.PartyReq;
import com.letsparty.vo.User;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.PartyProfileForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPartyApplicationService {

	private final UserPartyApplicationMapper userPartyApplicationMapper;
	private final UserProfileMapper userProfileMapper;
	private final UserMapper userMapper;
	private final PartyReqMapper partyReqMapper;
	private final PartyMapper partyMapper;
	@Value("${s3.path.covers}")
	private String coversPath;
	@Value("${s3.path.profiles}")
	private String profilePath;
	
	public void addLeaderUserPartyApplication(int partyNo, String leaderId) {
		addUserPartyApplicationWithApproved(partyNo, leaderId, 6, userProfileMapper.getDefaultProfileById(leaderId));
	}
	
	public void addUserPartyApplicationWithApproved(int partyNo, String userId, int roleNo, UserProfile userProfile) {
		User user = new User();
		user.setId(userId);
		UserPartyApplication userPartyApplication = new UserPartyApplication();
		Party party = new Party();
		party.setNo(partyNo);
		userPartyApplication.setParty(party);
		userPartyApplication.setUser(user);
		userPartyApplication.setRoleNo(roleNo);
		userPartyApplication.setUserProfile(userProfile);
		userPartyApplication.setStatus("승인");
		userPartyApplicationMapper.insert(userPartyApplication);
	}
	
	public boolean addUserPartyApplicationIfReqMet(int partyNo, String userId, UserProfile userProfile) {
		List<PartyReq> partyReq = partyReqMapper.getPartyReqsByNo(partyNo);
		User user = userMapper.getUserById(userId);
		// 생년 제한 검사
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(user.getBirthday());
		int userYear = calendar.get(Calendar.YEAR);						// 1995
		int birthStart = Integer.parseInt(partyReq.get(1).getValue());	// 1985
		int birthEnd = Integer.parseInt(partyReq.get(0).getValue());	// 2010
		if (!(userYear >= birthStart && userYear <= birthEnd)) {
			return false;
		}
					
		// 성별 제한 검사
		if (!("A".equals(partyReq.get(2).getValue()) || partyReq.get(2).getValue().equals(user.getGender()))) {
			return false;
		}
		
		addUserPartyApplicationWithApproved(partyNo, userId, 8, userProfile);
		
		return true;
	}
	
	public UserPartyApplication findByPartyNoAndUserId(int partyNo, String userId) {
		UserPartyApplication upa = userPartyApplicationMapper.findByPartyNoAndUserId(partyNo, userId);
		if (upa != null && !upa.getUserProfile().getIsUrl()) {
			upa.getUserProfile().setFilename(profilePath + upa.getUserProfile().getFilename());
		}
		return upa;
	}
	
	public int countPartyMemberWithStatus(int partyNo, String status) {
		return userPartyApplicationMapper.countPartyMemberByPartyNoAndStatus(partyNo, status);
	}

	public List<UserPartyApplication> findAllByUserId(String userId) {
		List<UserPartyApplication> upaList = userPartyApplicationMapper.findAllByUserId(userId);
		for (UserPartyApplication upa : upaList) {
			upa.getParty().setFilename(coversPath + upa.getParty().getFilename());
		}
		return upaList;
	}
	
	public List<UserPartyApplication> findAllExceptMemberByUserId(String userId) {
		List<UserPartyApplication> upaList = userPartyApplicationMapper.findAllExceptMemberByUserId(userId);
		return upaList;
	}
	
	public void kick(UserPartyApplication savedUserPartyApplication, Party savedParty) {
		savedUserPartyApplication.setStatus("강퇴");
		savedParty.setCurCnt(savedParty.getCurCnt() -1);
		userPartyApplicationMapper.update(savedUserPartyApplication);
		partyMapper.updateParty(savedParty);
	}
	
	public boolean isLeader(LoginUser loginUser) {
		if (loginUser != null && !userPartyApplicationMapper.findAllExceptMemberByUserId(loginUser.getId()).isEmpty()) {
			return true;
		}
		return false;
	}

	public void updatePartyProfile(PartyProfileForm partyProfileForm, String id) {
		for (int i = 0; i < partyProfileForm.getUpaNo().length; i++) {
			UserPartyApplication savedUpa = userPartyApplicationMapper.findByNo(partyProfileForm.getUpaNo()[i]);
			savedUpa.setUserProfile(UserProfile.builder().no(partyProfileForm.getProfileNo()[i]).build());
			if (savedUpa.getUser().getId().equals(id)) {
				userPartyApplicationMapper.update(savedUpa);
			}
		}
	}

	public void withdraw(int upaNo) {
		userPartyApplicationMapper.withdraw(upaNo);
	}

	public boolean isDeletable(int partyNo, String leaderId) {
	    int memberCount = userPartyApplicationMapper.countApprovedMembersExceptLeader(partyNo, leaderId);
	    return memberCount == 0;
	}

}
