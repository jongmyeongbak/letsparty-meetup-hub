package com.letsparty.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserProfileMapper;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.vo.UserProfile;
import com.letsparty.web.form.PartyProfileForm;
import com.letsparty.web.form.UserProfileForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
	
	private final UserProfileMapper userProfileMapper;
	@Value("${s3.path.profiles}")
	private String profilePath;
	
	public int addProfile(UserProfileForm userProfileForm) {
		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		
		userProfileMapper.addProfile(userProfile);
		
		return userProfile.getNo();
	}
	
	public void addProfileWithLogin(UserProfileForm userProfileForm) {
		UserProfile userProfile = new UserProfile();
		BeanUtils.copyProperties(userProfileForm, userProfile);
		if (userProfile.getFilename().isBlank()) {
			userProfile.setFilename("/images/party/profile-default.png");
			userProfile.setIsUrl(true);
		}
		if (userProfile.getNickname().isBlank()) {
			userProfile.setNickname("이름없음");
		}
		if (null == userProfile.getIsDefault()) {
			userProfile.setIsDefault(false);
		} else if (userProfile.getIsDefault()) {
			// 기본으로 설정했다면 해당 아이디로 모두 불러와서 해당 프로필을 제외한 모든 프로필의 default값을 false로 변경
			List<UserProfile> profileList = userProfileMapper.getProfileByUserId(userProfile.getId());
			for (UserProfile profile : profileList) {
				if (profile.getNo() != userProfile.getNo()) {
					profile.setIsDefault(false);
					userProfileMapper.updateProfile(profile);
				}
			}
		}
		userProfileMapper.addProfile(userProfile);
	}
	
	public void changeProfile(UserProfileForm userProfileForm) {

		UserProfile userProfile = new UserProfile();
		
		BeanUtils.copyProperties(userProfileForm, userProfile);
		System.out.println(userProfileForm.toString());
		
		UserProfile savedUserProfile = userProfileMapper.getProfileByNo(userProfile.getNo());
		
		// 사진을 변경하지 않았을 때(닉네임만 바꾼경우, 혹은 아무것도 안 했는데 변경을 눌렀을 때)
		if (userProfile.getFilename().isBlank()) {
			userProfile.setFilename(savedUserProfile.getFilename());
			if (null == userProfileForm.getIsUrl()) {
				userProfile.setIsUrl(savedUserProfile.getIsUrl());
			}
		}
		
		// 기본으로 설정하지 않았다면 false
		if (null == userProfile.getIsDefault()) {
			userProfile.setIsDefault(false);
		} else if (userProfile.getIsDefault()) {
			// 기본으로 설정했다면 해당 아이디로 모두 불러와서 해당 프로필을 제외한 모든 프로필의 default값을 false로 변경
			List<UserProfile> profileList = userProfileMapper.getProfileByUserId(userProfile.getId());
			for (UserProfile profile : profileList) {
				if (profile.getNo() != userProfile.getNo()) {
					profile.setIsDefault(false);
					userProfileMapper.updateProfile(profile);
				}
			}
		}
		
		userProfileMapper.updateProfile(userProfile);
	}
	
	public UserProfile getDefaultProfile(String id) {
		UserProfile userProfile = userProfileMapper.getDefaultProfileById(id);
		
		return userProfile;
	}

	public List<UserProfile> getAllProfileByUserId(String id) {
		List<UserProfile> profileList = userProfileMapper.getProfileByUserId(id);
		for (UserProfile profile : profileList) {
			if (!profile.getIsUrl()) {
				profile.setFilename(profilePath + profile.getFilename());
			}
		}
		return profileList;
	}

	public UserProfile getProfileByNo(int profileNo) {
		UserProfile profile = userProfileMapper.getProfileByNo(profileNo); 
		if (!profile.getIsUrl()) {
			profile.setFilename(profilePath + profile.getFilename());
		}
		return profile;
	}

	public void deleteProfile(int profileNo, String id) {
		UserProfile savedProfile = userProfileMapper.getProfileByNo(profileNo);
		if (savedProfile.getId().equals(id)) {
			userProfileMapper.deleteProfileByProfileNo(profileNo);
		}
	}
}
