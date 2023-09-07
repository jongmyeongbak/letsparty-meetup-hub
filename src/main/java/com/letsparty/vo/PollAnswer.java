package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PollAnswer{
	
	private int no;
	private User id;
	private PollOption pollOptionNo;
}
