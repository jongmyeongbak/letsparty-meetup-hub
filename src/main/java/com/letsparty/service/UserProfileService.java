package com.letsparty.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserProfileMapper;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
	
	private final UserProfileMapper userProfileMapper;
	
	public int addProfile(UserProfileForm userProfileForm) {
		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		userProfileMapper.addProfile(userProfile);
		
		return userProfile.getNo();
	}

	public void changeProfile(UserProfileForm userProfileForm) {

		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		UserProfile savedUserProfile = userProfileMapper.getProfileByNo(userProfile.getNo());
		
		if (userProfile.getFilename() != savedUserProfile.getFilename()) {
			userProfile.setIsUrl(false);
		}
		
		userProfileMapper.updateProfile(userProfile);
	}
	
	public UserProfile getDefaultProfile(String id) {
		UserProfile userProfile = userProfileMapper.getDefaultProfileById(id);
		
		return userProfile;
	}

	public List<UserProfile> getAllProfileByUserId(String id) {
;		return userProfileMapper.getProfileByUserId(id);
	}
	
}
