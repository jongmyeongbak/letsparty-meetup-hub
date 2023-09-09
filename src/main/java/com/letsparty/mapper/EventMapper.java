package com.letsparty.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Event;

@Mapper
public interface EventMapper {

	// 신규 일정 등록하기
	void insertEvent(Event event);
	
	// 등록된 일정 수정하기
	void updateEvent(int id);
	
	// 등록한 일정 상세정보 조회하기
	Event getEventDetailByNo(int id);
	
	// 일정번호로 일정 가져오기
	Event getEventByNo(int id);
	
	// 일정목록 가져오기
	List<Event> getEvents(int partyNo, LocalDateTime startDate, LocalDateTime endDate);
	
	// 등록한 일정 삭제하기
	void deleteEvent(int id);
	
	void updateEvent(Event event);
	
	// 파티번호로 5개 일정 가져오기
	List<Event> getEventsByPartyNo(int partyNo);
}