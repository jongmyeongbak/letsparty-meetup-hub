package com.letsparty.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {

	private int id;
	@JsonIgnore
	private Party party;
	private User user;
	private String title;
	private String description;
	private int status;
	private boolean allDay;
	private LocalDateTime start;
	private LocalDateTime end;
	private Post post;
	private Place place;
	
}