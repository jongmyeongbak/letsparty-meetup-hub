package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PartyReq {

	private Party party;
	private String name;
	private String value;
	private String description;
	
}
