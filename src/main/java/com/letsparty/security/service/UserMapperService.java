package com.letsparty.security.service;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserMapper;
import com.letsparty.vo.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMapperService {

	private final UserMapper userMapper;
	
	/*
	 * 유저 정보 가져오기 (id, email)
	 */
	public User getUserByName(String id) {
		User user = userMapper.getUserById(id);
		return user;
	}
	
	public User getUserByEmail(String email) {
		User user = userMapper.getUserByEmail(email);
		return user;
	}
}
