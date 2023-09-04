package com.letsparty.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.letsparty.security.user.LoginUser;
import com.letsparty.service.LetsPartyService;
import com.letsparty.service.PartyService;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HomeInterceptor implements HandlerInterceptor {
	
	private final PartyService partyService;
	private final LetsPartyService letsPartyService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		List<LetsPartyPost> lpPosts = letsPartyService.getPostsLimit5();
		List<Party> parties = partyService.getPartiesLimit5();
		
		modelAndView.addObject("lpPosts", lpPosts);
		modelAndView.addObject("parties", parties);
		
		// 로그인이 되면 내가 가입한 파티의 정보를 생성 날짜 기준으로 최근 5개를 가져와 표시한다.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			LoginUser user = (LoginUser) authentication.getPrincipal();
			List<Party> userParty = partyService.getpartyByUserId(user.getId());
			modelAndView.addObject("userParty", userParty);
		}
	}
}
