package com.letsparty.security.service;

import java.util.Date;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.UserMapper;
import com.letsparty.security.oauth.exception.OAuthProviderMissMatchException;
import com.letsparty.security.oauth.info.OAuth2UserInfo;
import com.letsparty.security.oauth.info.OAuth2UserInfoFactory;
import com.letsparty.security.user.CustomOAuth2User;
import com.letsparty.vo.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	
	private final UserMapper userMapper;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		
		// 로그인방식 가져오기(Naver, Kakao)
		String providerType = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getAuth2UserInfo(providerType, oAuth2User.getAttributes());
		User savedUser = userMapper.getUserById(userInfo.getId());
		
		if (savedUser != null) {
			if (!providerType.equals(savedUser.getProviderType())) {
				throw new OAuthProviderMissMatchException("소셜로그인 공급자가 일치하지 않습니다.");
			}
			
		} else {
			savedUser = createUser(userInfo, providerType);
		}
		
		return new CustomOAuth2User(savedUser, userInfo.getAttributes());
	}
	
	private User createUser(OAuth2UserInfo userInfo, String providerType) {
		User user = new User();
		user.setId(userInfo.getId());
		user.setEmail(userInfo.getEmail());
		user.setName(userInfo.getName());
		user.setGender('M');
		user.setProviderType(providerType);
		user.setTel("010-2313-1231");
		user.setBirthday(new Date());
		
		userMapper.createUser(user);
		
		return user;
	}
	


}
