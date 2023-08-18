package com.letsparty.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.MyMapper;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyService {
	
	private final MyMapper myMapper;
	
	public int addProfile(UserProfileForm userProfileForm) {
		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		myMapper.addProfile(userProfile);
		
		return userProfile.getNo();
	}

	public void changeProfile(UserProfileForm userProfileForm) {

		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		myMapper.updateProfile(userProfile);
	}

}
