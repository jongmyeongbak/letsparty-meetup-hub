package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Place {

	private long no;
	private String id;
	private String name;
	private String addressName;
	private String roadAddressName;
	private Post post;
	
}
