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
            .excludePathPatterns("/css/**", "/js/**", "/images/**",
            		"/party/{partyNo}",
            		"/party/{partyNo}/read/{postNo}",
            		"/party/{partyNo}/post/{postId}/comment",
            		"/party/{partyNo}/withdraw/{upaNo}",
            		"/my/profile/{profileNo}");
      registry.addInterceptor(partyInterceptor)
            .addPathPatterns("/party/{partyNo}/**")
            .excludePathPatterns(
            		"/party/{partyNo}",
            		"/party/{partyNo}/read/{postNo}",
            		"/party/{partyNo}/post/{postId}/comment",
            		"/party/{partyNo}/withdraw/{upaNo}");
   }
}