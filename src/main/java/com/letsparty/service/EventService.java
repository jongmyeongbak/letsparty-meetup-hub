package com.letsparty.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.EventMapper;
import com.letsparty.vo.Event;
import com.letsparty.vo.User;
import com.letsparty.web.form.RegisterEventForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventMapper eventMapper;
	
	// 일정 정보 등록하기
	public Event insertEvent(User user, RegisterEventForm form) {
		Event event = new Event();
		BeanUtils.copyProperties(form, event);
		event.setUser(user);
		
		eventMapper.insertEvent(event);
		
		return event;
	}
	
	// 일정 목록 조회하기
	public List<Event> getAllEvents() {
		List<Event> events = eventMapper.getAllEvents();
		return events;
	}
	
	// 일정 상세정보 조회하기
	public Event getEventDetail(int evnetNo) {
		return eventMapper.getEventDetailByNo(evnetNo);
	}
	
}
