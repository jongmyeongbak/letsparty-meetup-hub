package com.letsparty.security.user;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.letsparty.vo.User;

public class CustomOAuth2User extends LoginUser implements OAuth2User {
	
	private String providerType;
	private Map<String, Object> attributes;

	public CustomOAuth2User(User user, Map<String, Object> attributes) {
		super(user);
		this.providerType = user.getProviderType();
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getName() {
		return getId();
	}
	
	public String providerType() {
		return providerType;
	}

	
}
