package com.letsparty.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.MyMapper;
import com.letsparty.mapper.UserMapper;
import com.letsparty.mapper.UserRoleMapper;
import com.letsparty.security.oauth.exception.OAuthProviderMissMatchException;
import com.letsparty.security.oauth.info.OAuth2UserInfo;
import com.letsparty.security.oauth.info.OAuth2UserInfoFactory;
import com.letsparty.security.user.CustomOAuth2User;
import com.letsparty.vo.User;
import com.letsparty.vo.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	
	private final UserMapper userMapper;
	private final UserRoleMapper userRoleMapper;
	private final MyMapper myMapper;

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
			UserProfile userProfile = UserProfile.builder()
					.id(userInfo.getId())
					.nickname(userInfo.getName())
					.filename(userInfo.getImage())
					.isDefault(true)
					.build();
			log.info("이미지URL -> {}", userInfo.getImage());
			
			myMapper.addProfile(userProfile);
		}
		
		String roleName = userRoleMapper.getRoleNameById(userInfo.getId());
		CustomOAuth2User user = new CustomOAuth2User(savedUser, userInfo.getAttributes());
		user.setRoleName(roleName);
		
		return user;
	}
	
	private User createUser(OAuth2UserInfo userInfo, String providerType) {
		User user = new User();
		user.setId(userInfo.getId());
		user.setEmail(userInfo.getEmail());
		user.setName(userInfo.getName());
		user.setProviderType(providerType);
		
		userMapper.createUser(user);
		
		Map<String, Object> param = new HashMap<>();
		param.put("id", user.getId());
		param.put("no", 5);
		
		userRoleMapper.addRole(param);
		
		return user;
	}
}
