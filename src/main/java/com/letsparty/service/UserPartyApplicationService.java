package com.letsparty.service;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserPartyApplicationMapper;
import com.letsparty.mapper.UserProfileMapper;
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
		userPartyApplication.setPartyNo(partyNo);
		userPartyApplication.setUser(user);
		userPartyApplication.setRoleNo(roleNo);
		userPartyApplication.setUserProfile(userProfile);
		userPartyApplication.setStatus("승인");
		userPartyApplicationMapper.insert(userPartyApplication);
	}
}
