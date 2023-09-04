package com.letsparty.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.letsparty.web.interceptor.HomeInterceptor;
import com.letsparty.web.interceptor.PartyInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private final PartyInterceptor partyInterceptor;
	private final HomeInterceptor homeInterceptor;
	
	@Autowired
	public WebConfig(PartyInterceptor partyInterceptor, HomeInterceptor homeInterceptor) {
		this.partyInterceptor = partyInterceptor;
		this.homeInterceptor = homeInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(partyInterceptor).addPathPatterns("/party/{partyNo}/**");
		registry.addInterceptor(homeInterceptor).addPathPatterns("/");
	}
}