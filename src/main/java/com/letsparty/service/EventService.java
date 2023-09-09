package com.letsparty.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.EventMapper;
import com.letsparty.vo.Event;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.web.form.RegisterEventForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventMapper eventMapper;
	
	// 일정 정보 등록하기
	public Event insertEvent(RegisterEventForm registerEventForm, String userId) {
		// TODO 파티권한 확인
		Event event = new Event();
		BeanUtils.copyProperties(registerEventForm, event);
		Party party = new Party();
		party.setNo(registerEventForm.getPartyNo());
		event.setParty(party);
		User user = new User();
		user.setId(userId);
		event.setUser(user);
		
		eventMapper.insertEvent(event);
		
		return event;
	}    
	
	public boolean updateEvent(int id, RegisterEventForm updateEventForm, String userId) {
		// TODO 파티권한 확인
		Event existingEvent = eventMapper.getEventByNo(id);
		if (existingEvent != null) {
			// 업데이트 내용을 updateEventForm에서 가져와 existingEvent 적용
			existingEvent.setTitle(updateEventForm.getTitle());
			existingEvent.setDescription(updateEventForm.getDescription());
			existingEvent.setAllDay(updateEventForm.isAllDay());
			existingEvent.setStart(updateEventForm.getStart());
			existingEvent.setEnd(updateEventForm.getEnd());
			eventMapper.updateEvent(existingEvent);
			return true;
		}
		return false;
	}
	
	// 일정 목록 조회하기
	public List<Event> getEvents(int partyNo, Date startDate, Date endDate) {
		return eventMapper.getEvents(partyNo, convertDateToLocalDateTime(startDate), convertDateToLocalDateTime(endDate));
	}
	
	// 일정 상세정보 조회하기
	public Event getEventDetail(int id) {
		return eventMapper.getEventDetailByNo(id);
	}
	
	public Event getEventById(int id) {
		return eventMapper.getEventByNo(id);
	}
	
	private LocalDateTime convertDateToLocalDateTime(Date date) {
        // Date 객체를 Instant 객체로 변환
        Instant instant = date.toInstant();
        // Instant 객체를 LocalDateTime 객체로 변환
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
	}
	
	// 일정 목록 조회(파티번호로 최근 5개까지만 조회)
	public List<Event> getEventsByPartyNo(int partyNo){
		return eventMapper.getEventsByPartyNo(partyNo);
	}
}