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
//	Post 객체의 id를 담아야 하는데 현재 생성이 안
	private int postId;
	
}
