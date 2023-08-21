package com.letsparty.security.oauth.info;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String) getAttributes().get("sub");
	}

	@Override
	public String getName() {
		return (String) getAttributes().get("name");
	}

	@Override
	public String getEmail() {
		return (String) getAttributes().get("email");
	}

	@Override
	public String getImage() {
		// TODO Auto-generated method stub
		return null;
	}	

	
}
