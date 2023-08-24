package com.letsparty.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserMapper;
import com.letsparty.mapper.UserRoleMapper;
import com.letsparty.security.user.CustomUserDetails;
import com.letsparty.vo.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;
	private final UserRoleMapper userRoleMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User savedUser = userMapper.getUserById(username);
		if (savedUser == null) {
			throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
		}
		CustomUserDetails user = new CustomUserDetails(savedUser);
		
		String roleName = userRoleMapper.getRoleNameById(user.getId());
		
		user.setRoleName(roleName);
		return user;
	}
}
