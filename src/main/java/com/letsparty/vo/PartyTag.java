package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PartyTag {

	Category category;
	Party party;
	String name;
}
