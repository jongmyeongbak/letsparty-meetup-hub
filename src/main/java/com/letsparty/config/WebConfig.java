package com.letsparty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.letsparty.web.interceptor.NavbarInterceptor;
import com.letsparty.web.interceptor.PartyInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final PartyInterceptor partyInterceptor;
	private final NavbarInterceptor navbarInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(navbarInterceptor)
				.addPathPatterns("/", "/party/**", "/letsparty/**", "/my/**", "/party-create")
				.excludePathPatterns("/css/**", "/js/**", "/images/**", "/party/{partyNo}/setting/kick", "/{partyNo}/post/{postId}/comment", "/my/profile/{profileNo}", "/my/delete/{profileNo}");
		registry.addInterceptor(partyInterceptor)
				.addPathPatterns("/party/{partyNo}/**")
				.excludePathPatterns("/party/{partyNo}/setting/kick");
	}
}