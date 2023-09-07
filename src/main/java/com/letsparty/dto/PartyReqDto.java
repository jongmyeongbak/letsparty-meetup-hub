package com.letsparty.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PartyReqDto {

	private String birthStart;
	private String birthEnd;
	private String gender;
}
