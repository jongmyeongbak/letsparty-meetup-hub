package com.letsparty.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyPostForm {
	
	private String title;
	private String content;
	private int partyNo;
}
