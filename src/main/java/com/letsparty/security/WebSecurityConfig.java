package com.letsparty.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.letsparty.security.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.formLogin(form -> form
				.loginPage("/login")
				.usernameParameter("id")
				.passwordParameter("password")
				.loginProcessingUrl("/login") 
				.defaultSuccessUrl("/")
				.failureUrl("/login?error=fail"))
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true))
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.failureUrl("/login?error=fail")
				.userInfoEndpoint()
				.userService(customOAuth2UserService))
			.exceptionHandling()
				.authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login?error=noauth"))
				.accessDeniedHandler((request, response, accessDeniedException) -> response.sendRedirect("/login?error=denied"));
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
