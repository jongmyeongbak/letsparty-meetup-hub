package com.letsparty.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserProfileMapper;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
	
	private final UserProfileMapper myMapper;
	
	public int addProfile(UserProfileForm userProfileForm) {
		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		myMapper.addProfile(userProfile);
		
		return userProfile.getNo();
	}

	public void changeProfile(UserProfileForm userProfileForm) {

		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		UserProfile savedUserProfile = myMapper.getProfileByNo(userProfile.getNo());
		
		if (userProfile.getFilename() != savedUserProfile.getFilename()) {
			userProfile.setIsUrl(false);
		}
		
		myMapper.updateProfile(userProfile);
	}
	
	public UserProfile getDefaultProfile(String id) {
		UserProfile userProfile = myMapper.getDefaultProfileById(id);
		
		return userProfile;
	}
	
}
