package com.letsparty.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterEventForm {

	private String title;
	private String description;
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	private int allDay;
	private int partyNo;
}
