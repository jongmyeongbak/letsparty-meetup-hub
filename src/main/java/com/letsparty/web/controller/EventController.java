package com.letsparty.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.letsparty.service.EventService;
import com.letsparty.vo.Event;
import com.letsparty.vo.User;
import com.letsparty.web.form.RegisterEventForm;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/event")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	//@PreAuthorize("isAuthenticated()")
	@PostMapping("/register")
	@ResponseBody
	public Event registerEvent(@AuthenticationPrincipal User user, RegisterEventForm form) {
		user = new User();
		user.setId("hong");
		Event event = eventService.insertEvent(user, form);	
		log.info("일정 정보 -> {}", form);
		
		return event;
	}
	
	@GetMapping("/list")
	public String eventList(Model model) {
		List<Event> events = eventService.getAllEvents();
		model.addAttribute(events);
		
		return "party/event";
	}
}
