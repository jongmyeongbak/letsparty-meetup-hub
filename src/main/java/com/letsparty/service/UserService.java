package com.letsparty.service;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserMapper;
import com.letsparty.vo.User;
import com.letsparty.web.form.SignupForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	/*
	 * 유저 정보 저장(회원가입)
	 */
	public void signupUser(SignupForm signupForm) {
		User user = new User();
		BeanUtils.copyProperties(signupForm, user);
		String encryptedPassword = passwordEncoder.encode(signupForm.getPassword());
		user.setPassword(encryptedPassword);
		
		userMapper.createUser(user);
	}

}
