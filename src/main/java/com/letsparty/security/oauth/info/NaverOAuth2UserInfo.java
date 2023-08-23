package com.letsparty.security.oauth.info;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo{

	public NaverOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getId() {
		Map<String, Object> properties = (Map<String, Object>)getAttributes().get("response");

		return (String) properties.get("id");
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getName() {
		Map<String, Object> properties = (Map<String, Object>)getAttributes().get("response");

		return (String) properties.get("name");
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getEmail() {
		Map<String, Object> properties = (Map<String, Object>)getAttributes().get("response");
		
		return (String) properties.get("email");
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getImage() {
		Map<String, Object> properties = (Map<String, Object>)getAttributes().get("response");
		
		return (String) properties.get("profile_image");
	}
}
