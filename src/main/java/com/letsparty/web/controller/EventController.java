package com.letsparty.web.controller;


import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letsparty.service.EventService;
import com.letsparty.vo.Event;
import com.letsparty.web.form.RegisterEventForm;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	
	
	//@PreAuthorize("isAuthenticated()")
	@PostMapping("/register")
	public Event registerEvent(RegisterEventForm registerEventform, Principal principal) {
		Event event = eventService.insertEvent(registerEventform, principal.getName());	
		log.info("일정 정보 -> {}", registerEventform);
		
		return event;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Event> getEventById(@PathVariable int id) {
		// evetnNo를 사용하여 이벤트 조회
		Event event = eventService.getEventById(id);
		
		if (event != null) {
			log.info("event: {}", event);
			return ResponseEntity.ok(event);
		} else {
			log.error("event 오류");
			return ResponseEntity.noContent().build();
		}
	}
	
	@PostMapping("/update/{id}")
	public ResponseEntity<String> updateEvent(@PathVariable int id, @ModelAttribute RegisterEventForm updateEventForm, Principal principal) {
		if (eventService.updateEvent(id, updateEventForm, principal.getName())) {
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.badRequest().body("fail");
	}
	
	
	@GetMapping("/events")
	public List<Event> getEvents(int partyNo, @RequestParam("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
		return eventService.getEvents(partyNo, startDate, endDate);
	}
	
	
}