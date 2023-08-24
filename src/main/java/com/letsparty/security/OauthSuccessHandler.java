package com.letsparty.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.letsparty.security.service.UserMapperService;
import com.letsparty.security.user.CustomOAuth2User;
import com.letsparty.vo.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OauthSuccessHandler implements AuthenticationSuccessHandler {
	
	private final UserMapperService userMapperService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		User user = userMapperService.getUserByEmail(customOAuth2User.getEmail());
		
		if (user.getBirthday() != null && user.getGender() != null) {
			response.sendRedirect("/");
		} else {
			response.sendRedirect("/signup/oauth-signup");
		}
		
	}
	
}
