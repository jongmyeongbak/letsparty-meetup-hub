package com.letsparty.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserRoleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final UserRoleMapper userRoleMapper;
	
	public void changeAuthentication(String id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		List<GrantedAuthority> newAuthority = new ArrayList<>();
		
		String roleName = userRoleMapper.getRoleNameById(id);
		
		newAuthority.add(new SimpleGrantedAuthority(roleName));
		
		Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), newAuthority);
		
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}
}
