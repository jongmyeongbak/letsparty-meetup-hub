package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.UserProfile;

@Mapper
public interface UserProfileMapper {

	void addProfile(UserProfile userProfile);

	void updateProfile(UserProfile userProfile);

	UserProfile getDefaultProfileById(String id);
	
	UserProfile getProfileByNo(int no);

	List<UserProfile> getProfileByUserId(String id);

}
