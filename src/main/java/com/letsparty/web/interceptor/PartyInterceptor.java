package com.letsparty.web.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.letsparty.dto.ChatRoomWithUsers;
import com.letsparty.dto.ChatUserResponse;
import com.letsparty.dto.EventDto;
import com.letsparty.security.user.LoginUser;
import com.letsparty.service.ChatService;
import com.letsparty.service.EventService;
import com.letsparty.service.PartyService;
import com.letsparty.service.UserPartyApplicationService;
import com.letsparty.util.TimeConverter;
import com.letsparty.vo.Event;
import com.letsparty.vo.Party;
import com.letsparty.vo.UserPartyApplication;

@Component
public class PartyInterceptor implements HandlerInterceptor {

	private final PartyService partyService;
	private final UserPartyApplicationService userPartyApplicationService;
	private final ChatService chatService;
	private final EventService eventService;
	@Value("${s3.path.covers}")
	private String coversPath;
	@Value("${s3.path.profiles}")
	private String profilePath;
	
	@Autowired
	public PartyInterceptor(PartyService partyService,  UserPartyApplicationService userPartyApplicationService, ChatService chatService, EventService eventService) {
		this.partyService = partyService;
		this.userPartyApplicationService = userPartyApplicationService;
		this.chatService = chatService;
		this.eventService = eventService;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		
		String uri = request.getRequestURI();
		int partyNo = Integer.parseInt(uri.split("/")[2]);
		
		Party party = partyService.getPartyByNo(partyNo);
		party.setFilename(coversPath + party.getFilename());
		
		// 로그인을 하고 해당 파티에 가입된 경우엔 right.html과 채팅방 정보와 일정을 가져온다.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			LoginUser user = (LoginUser) authentication.getPrincipal();
			
			
			UserPartyApplication upa =  userPartyApplicationService.findByPartyNoAndUserId(partyNo, user.getId());
			if (upa != null && upa.getStatus().equals("승인")) {
				// 내가 가입한 파티의 채팅방 목록 불러오기(내가 참여중인 방만)
				List<ChatRoomWithUsers> chatRooms = chatService.getChatRoomByPartyNoAndUserId(partyNo, user.getNo());
				for (ChatRoomWithUsers chatroom : chatRooms) {
					// StringJoiner를 사용해서 여러 사용자의 이름을 ","로 이어서 저장함
					StringJoiner sj = new StringJoiner(",");
					for (ChatUserResponse chatUser : chatroom.getChatUsers()) {
						if (chatUser.isUrl()) {
							chatUser.setFilename(profilePath + chatUser.getFilename());
						}
						if (!chatroom.isPublic()) {
							sj.add(chatUser.getNickname());
						}
					}
					chatroom.setChatUsersText(sj.toString());
				}
				
				// 내가 가입한 파티의 일정을 불러오기
				List<Event> getEvents = eventService.getEventsByPartyNo(partyNo);
				List<EventDto> events = new ArrayList<>();
				
				for(Event event : getEvents) {
					EventDto dto = new EventDto();
					dto.setDescription(event.getDescription());
					dto.setTitle(event.getTitle());
					String day = event.getStartDate().split("-")[2];
					String month = event.getStartDate().split("-")[1];
					dto.setStartDay(day);
					dto.setStartMonth(month);
					dto.setStartTime(TimeConverter.convertToAmPm(event.getStartTime()));
					events.add(dto);
				}
				modelAndView.addObject("events", events);
				modelAndView.addObject("chatRooms", chatRooms);
				modelAndView.addObject("member", user);
			}
		}
		
		UserPartyApplication leadrOfParty = userPartyApplicationService.findByPartyNoAndUserId(partyNo, party.getLeader().getId());
		
		modelAndView.addObject("party", party);
		modelAndView.addObject("leader", leadrOfParty);
		modelAndView.addObject("partyNo", partyNo);
	}
}
