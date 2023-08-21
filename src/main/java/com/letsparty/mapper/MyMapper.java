package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.UserProfile;

@Mapper
public interface MyMapper {

	void addProfile(UserProfile userProfile);

	void updateProfile(UserProfile userProfile);

	UserProfile getDefaultProfileById(String id);

}
