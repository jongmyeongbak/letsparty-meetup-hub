package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.UserPartyApplication;

@Mapper
public interface UserPartyApplicationMapper {

	void insert(UserPartyApplication userPartyApplication);
	UserPartyApplication findByNo(long no);
	List<UserPartyApplication> findAllByPartyNo(int no);
	void update(UserPartyApplication userPartyApplication);
}
