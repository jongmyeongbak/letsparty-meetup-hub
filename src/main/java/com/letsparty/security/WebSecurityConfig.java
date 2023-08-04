package com.letsparty.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

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
