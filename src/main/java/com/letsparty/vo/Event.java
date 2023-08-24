package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {

	private int no;
	private String title;
	private String description;
	private int status;
	private int allDay;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
//	private Party party;
//	private Place place;
	private User user;
//	private Post post;
	
}
