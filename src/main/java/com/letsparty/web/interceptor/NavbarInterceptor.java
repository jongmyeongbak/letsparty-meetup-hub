package com.letsparty.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.letsparty.dto.UserChatSummaryDto;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NavbarInterceptor implements HandlerInterceptor {

	private final ChatService chatService;
	@Value("${s3.path.covers}")
	private String coversPath;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		if (!"GET".equals(request.getMethod())) {
			return;
		}
		
		String[] paths = request.getRequestURI().split("/");
		modelAndView.addObject("paths", paths);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean hasRegularRole = auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_USER_PENDING"));
		if (hasRegularRole) {
			LoginUser user = (LoginUser) auth.getPrincipal();
			List<UserChatSummaryDto> chatSummaryDtos = chatService.getUserChatSummary(user.getNo());
			modelAndView.addObject("chatSummaryDtos", chatSummaryDtos);
			modelAndView.addObject("coversPath", coversPath);
			int allChatCount = 0;
			for (UserChatSummaryDto chatSummaryDto : chatSummaryDtos) {
				allChatCount += chatSummaryDto.getMessageCount();
			}
			modelAndView.addObject("allChatCount", allChatCount);
		}
	}
}
