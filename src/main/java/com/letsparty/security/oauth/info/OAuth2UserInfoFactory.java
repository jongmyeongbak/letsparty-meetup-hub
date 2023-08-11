package com.letsparty.security.oauth.info;

import java.util.Map;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getAuth2UserInfo(String providerType, Map<String, Object> attributes) {
		switch (providerType) {
			case "google" :
				return new GoogleOAuth2UserInfo(attributes);
			case "kakao" :
				return new KakaoOAuth2UserInfo(attributes);
			default :
					throw new IllegalArgumentException("유효한소셜로그인 공급자가 아닙니다.");
		}
	}
}
