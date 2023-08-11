package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.User;

@Mapper
public interface UserMapper {

	// 신규 사용자 등록
	void createUser(User user);
	
	// 아이디로 회원 찾기
	User getUserById(String id);
	
	// 이메일로 회원 찾기
	User getUserByEmail(String email);
	
	// 사용자 정보 수정
	void updateUser(User user);
}
