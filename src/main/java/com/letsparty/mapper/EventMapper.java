package com.letsparty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Event;

@Mapper
public interface EventMapper {

	// 신규 일정 등록하기
	void insertEvent(Event event);
	
	// 등록한 일정 상세정보 조회하기
	Event getEventDetailByNo(int EventNo);
	
	// 일정번호로 일정 가져오기
	Event getEventByNo(int EnvetNo);
	
	// 일정목록 가져오기
	List<Event> getAllEvents();
	
	// 등록한 일정 삭제하기
	void deleteEvent(int eventNo);
	
}
