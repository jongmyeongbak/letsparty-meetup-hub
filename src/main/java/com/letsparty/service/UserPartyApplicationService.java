package com.letsparty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserPartyApplicationMapper;
import com.letsparty.mapper.UserProfileMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.vo.UserProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPartyApplicationService {

	private final UserPartyApplicationMapper userPartyApplicationMapper;
	private final UserProfileMapper userProfileMapper;
	
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
	
	public UserPartyApplication findByPartyNoAndUserId(int partyNo, String userId) {
		return userPartyApplicationMapper.findByPartyNoAndUserId(partyNo, userId);
	}
	
	public int countPartyMemberWithStatus(int partyNo, String status) {
		return userPartyApplicationMapper.countPartyMemberByPartyNoAndStatus(partyNo, status);
	}

	public List<UserPartyApplication> findAllByUserId(String userId) {
		return userPartyApplicationMapper.findAllByUserId(userId);
	}
	
	public boolean isLeader(LoginUser loginUser) {
		if (loginUser != null && !userPartyApplicationMapper.findAllByUserId(loginUser.getId()).isEmpty()) {
			return true;
		}
		return false;
	}
}
