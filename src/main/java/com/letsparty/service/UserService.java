package com.letsparty.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letsparty.exception.DuplicateEmailException;
import com.letsparty.exception.DuplicateUserIdException;
import com.letsparty.mapper.UserMapper;
import com.letsparty.mapper.UserRoleMapper;
import com.letsparty.vo.User;
import com.letsparty.web.form.SignupForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final UserRoleMapper userRoleMapper;
	private final PasswordEncoder passwordEncoder;
	
	/*
	 * 유저 정보 저장(회원가입)
	 */
	public void signupUser(SignupForm signupForm) {

		checkDuplicateUserId(signupForm.getId());
		checkDuplicateEmail(signupForm.getEmail());
		
		User user = new User();
		BeanUtils.copyProperties(signupForm, user);
		String encryptedPassword = passwordEncoder.encode(signupForm.getPassword());
		user.setPassword(encryptedPassword);
		
		userMapper.createUser(user);
		
		Map<String, Object> param = new HashMap<>();
		param.put("id", user.getId());
		param.put("no", 4);
		
		userRoleMapper.addRole(param);
	}
	
	public void checkDuplicateUserId(String id) {
		User savedUser = userMapper.getUserById(id);
		if (savedUser != null) {
			throw new DuplicateUserIdException(id);
		}
	}

	public void checkDuplicateEmail(String email) {
		User savedUser = userMapper.getUserByEmail(email);
		if (savedUser != null) {
			throw new DuplicateEmailException(email);
		}
	}
	
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

	public void updateUser(SignupForm signupForm) {
		User savedUser = new User();
		BeanUtils.copyProperties(signupForm, savedUser);
		
		userMapper.updateUser(savedUser);
	}
	public void updateRoleById(String id, int no) {
		Map<String, Object> param = new HashMap<>();
		param.put("id", id);
		param.put("no", no);
		userRoleMapper.updateRole(param);
	}

}
