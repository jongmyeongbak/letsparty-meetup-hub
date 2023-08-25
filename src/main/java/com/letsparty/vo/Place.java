package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Place {

	private long no;
	private String placeName;
	private String placeId;
	private String addressName;
	private String roadAddressName;
	private Post post;
	
}
